package com.edulibre.api.curso.service;

import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.security.service.CurrentUserService;
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
public class LeccionService {

    private final CursoRepository cursoRepository;
    private final LeccionRepository leccionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<LeccionResponse> listar(String cursoId) {
        cursoRepository.findByIdAndPublicadoTrue(cursoId)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        Usuario usuario = currentUserService.getCurrentUser().orElse(null);
        boolean autorizado = usuario != null && inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                usuario.getId(), cursoId, EstadoInscripcion.APROBADA);

        if (!autorizado) {
            throw new ForbiddenException("No tienes una inscripción aprobada en este curso");
        }

        return leccionRepository.findByCursoIdOrderByOrdenAsc(cursoId).stream()
                .map(this::toResponse)
                .toList();
    }

    private LeccionResponse toResponse(Leccion leccion) {
        return new LeccionResponse(
                leccion.getId(),
                leccion.getTitulo(),
                leccion.getOrden(),
                leccion.getYoutubeId(),
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
