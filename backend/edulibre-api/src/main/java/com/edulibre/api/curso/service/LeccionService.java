package com.edulibre.api.curso.service;

import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.shared.util.Rol;
import com.edulibre.api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class LeccionService {

    private final CursoRepository cursoRepository;
    private final LeccionRepository leccionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<LeccionResponse> listar(String cursoId) {
        Usuario usuario = currentUserService.getCurrentUser().orElse(null);
        boolean esAdmin = usuario != null && usuario.getRol() == Rol.ADMIN;

        Curso curso = cursoRepository.findByIdAndPublicadoTrue(cursoId)
                .orElse(null);

        if (curso == null) {
            if (!esAdmin) {
                throw new ResourceNotFoundException("Curso no encontrado");
            }
            curso = cursoRepository.findById(cursoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
        }

        boolean autorizado = esAdmin || (usuario != null && inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                usuario.getId(), cursoId, EstadoInscripcion.APROBADA));

        return leccionRepository.findByCursoIdOrderByOrdenAsc(curso.getId()).stream()
                .map(leccion -> toResponse(leccion, autorizado))
                .toList();
    }

    private LeccionResponse toResponse(Leccion leccion, boolean autorizado) {
        return new LeccionResponse(
                leccion.getId(),
                leccion.getTitulo(),
                leccion.getOrden(),
                autorizado ? leccion.getYoutubeId() : null,
                leccion.getDuracionMin(),
                duracion(leccion.getDuracionMin()));
    }

    private String duracion(Integer minutos) {
        if (minutos == null) {
            return null;
        }
        if (minutos < 60) {
            return minutos + " min";
        }
        int horas = minutos / 60;
        int resto = minutos % 60;
        return resto == 0 ? horas + "h" : horas + "h " + resto + " min";
    }
}
