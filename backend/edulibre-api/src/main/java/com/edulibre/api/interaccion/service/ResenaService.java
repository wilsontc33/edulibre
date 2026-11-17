package com.edulibre.api.interaccion.service;

import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.interaccion.dto.ResenaRequest;
import com.edulibre.api.interaccion.dto.ResenaResponse;
import com.edulibre.api.interaccion.entity.Resena;
import com.edulibre.api.interaccion.repository.ResenaRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.exception.ForbiddenException;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<ResenaResponse> listar(String cursoId) {
        cursoRepository.findByIdAndPublicadoTrue(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        return resenaRepository.findByCursoIdOrderByCreadoEnDesc(cursoId).stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public ResenaResponse crear(String cursoId, ResenaRequest request) {
        Usuario usuario = currentUserService.requireCurrentUser();
        Curso curso = cursoRepository.findByIdAndPublicadoTrue(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        boolean aprobada = inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                usuario.getId(), cursoId, EstadoInscripcion.APROBADA);
        if (!aprobada) {
            throw new ForbiddenException("Solo puedes reseñar cursos en los que estés inscrito y aprobado");
        }

        if (resenaRepository.existsByUsuarioIdAndCursoId(usuario.getId(), cursoId)) {
            throw new ConflictException("Ya has publicado una reseña para este curso");
        }

        Resena resena = Resena.builder()
                .curso(curso)
                .usuario(usuario)
                .calificacion(request.calificacion())
                .comentario(request.comentario().trim())
                .build();

        return toResponse(resenaRepository.save(resena));
    }

    private ResenaResponse toResponse(Resena resena) {
        return new ResenaResponse(
                resena.getId(),
                resena.getUsuario().getNombre(),
                resena.getCalificacion(),
                resena.getComentario(),
                resena.getCreadoEn());
    }
}
