package com.edulibre.api.interaccion.service;

import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.interaccion.dto.ProgresoResponse;
import com.edulibre.api.interaccion.entity.ProgresoLeccion;
import com.edulibre.api.interaccion.repository.ProgresoLeccionRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ForbiddenException;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.usuario.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProgresoService {

    private final LeccionRepository leccionRepository;
    private final ProgresoLeccionRepository progresoLeccionRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CurrentUserService currentUserService;

    @Transactional
    public ProgresoResponse completar(String leccionId) {
        Usuario usuario = currentUserService.requireCurrentUser();
        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Lección no encontrada"));

        String cursoId = leccion.getCurso().getId();
        requireInscripcionAprobada(usuario, cursoId);

        if (!progresoLeccionRepository.existsByUsuarioIdAndLeccionId(usuario.getId(), leccionId)) {
            progresoLeccionRepository.save(ProgresoLeccion.builder()
                    .usuario(usuario)
                    .leccion(leccion)
                    .build());
        }

        return calcular(usuario.getId(), cursoId);
    }

    @Transactional(readOnly = true)
    public ProgresoResponse obtener(String cursoId) {
        Usuario usuario = currentUserService.requireCurrentUser();
        requireInscripcionAprobada(usuario, cursoId);
        return calcular(usuario.getId(), cursoId);
    }

    private ProgresoResponse calcular(String usuarioId, String cursoId) {
        long total = leccionRepository.countByCursoId(cursoId);
        var completadasIds = progresoLeccionRepository.findLeccionIdsByUsuarioIdAndCursoId(usuarioId, cursoId);
        long completadas = completadasIds.size();
        int porcentaje = total == 0 ? 0 : (int) Math.round(completadas * 100.0 / total);
        return new ProgresoResponse(cursoId, total, completadas, porcentaje, completadasIds);
    }

    private void requireInscripcionAprobada(Usuario usuario, String cursoId) {
        boolean aprobada = inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                usuario.getId(), cursoId, EstadoInscripcion.APROBADA);
        if (!aprobada) {
            throw new ForbiddenException("No tienes una inscripción aprobada en este curso");
        }
    }
}
