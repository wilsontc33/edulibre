package com.edulibre.api.inscripcion.service;

import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.inscripcion.dto.AdminInscripcionResponse;
import com.edulibre.api.inscripcion.dto.CrearInscripcionRequest;
import com.edulibre.api.inscripcion.dto.InscripcionResponse;
import com.edulibre.api.inscripcion.entity.Inscripcion;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InscripcionService {

    private final InscripcionRepository inscripcionRepository;
    private final CursoRepository cursoRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public InscripcionResponse crear(CrearInscripcionRequest request) {
        Usuario usuario = currentUserService.requireCurrentUser();
        Curso curso = cursoRepository.findByIdAndPublicadoTrue(request.cursoId())
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        if (inscripcionRepository.existsByUsuarioIdAndCursoId(usuario.getId(), curso.getId())) {
            throw new ConflictException("Ya tienes una postulación para este curso");
        }

        Inscripcion inscripcion = Inscripcion.builder()
                .usuario(usuario)
                .curso(curso)
                .estado(EstadoInscripcion.PENDIENTE)
                .motivacion(request.motivacion().trim())
                .nivelPrevio(request.nivelPrevio().trim())
                .horasSemana(request.horasSemana())
                .build();

        return toResponse(inscripcionRepository.save(inscripcion));
    }

    @Transactional(readOnly = true)
    public List<InscripcionResponse> mis() {
        Usuario usuario = currentUserService.requireCurrentUser();
        return inscripcionRepository.findByUsuarioIdOrderByCreadoEnDesc(usuario.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<AdminInscripcionResponse> listar(EstadoInscripcion estado) {
        EstadoInscripcion filtro = estado == null ? EstadoInscripcion.PENDIENTE : estado;
        return inscripcionRepository.findByEstadoOrderByCreadoEnAsc(filtro).stream()
                .map(this::toAdminResponse)
                .toList();
    }

    @Transactional
    public AdminInscripcionResponse aprobar(String id) {
        return toAdminResponse(resolver(id, EstadoInscripcion.APROBADA, null));
    }

    @Transactional
    public AdminInscripcionResponse rechazar(String id, String comentarioAdmin) {
        return toAdminResponse(resolver(id, EstadoInscripcion.RECHAZADA, comentarioAdmin.trim()));
    }

    private Inscripcion resolver(String id, EstadoInscripcion nuevoEstado, String comentarioAdmin) {
        Inscripcion inscripcion = inscripcionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscripción no encontrada"));

        if (inscripcion.getEstado() != EstadoInscripcion.PENDIENTE) {
            throw new ConflictException("Solo se pueden resolver inscripciones PENDIENTES");
        }

        inscripcion.setEstado(nuevoEstado);
        inscripcion.setComentarioAdmin(comentarioAdmin);
        inscripcion.setResueltoEn(LocalDateTime.now());
        return inscripcionRepository.save(inscripcion);
    }

    private InscripcionResponse toResponse(Inscripcion inscripcion) {
        return new InscripcionResponse(
                inscripcion.getId(),
                inscripcion.getCurso().getId(),
                inscripcion.getCurso().getTitulo(),
                inscripcion.getEstado().name(),
                inscripcion.getEstado() == EstadoInscripcion.RECHAZADA
                        ? inscripcion.getComentarioAdmin()
                        : null,
                inscripcion.getCreadoEn(),
                inscripcion.getResueltoEn());
    }

    private AdminInscripcionResponse toAdminResponse(Inscripcion inscripcion) {
        return new AdminInscripcionResponse(
                inscripcion.getId(),
                inscripcion.getUsuario().getNombre(),
                inscripcion.getUsuario().getEmail(),
                inscripcion.getCurso().getId(),
                inscripcion.getCurso().getTitulo(),
                inscripcion.getNivelPrevio(),
                inscripcion.getMotivacion(),
                inscripcion.getEstado().name(),
                inscripcion.getComentarioAdmin(),
                inscripcion.getCreadoEn(),
                inscripcion.getResueltoEn());
    }
}
