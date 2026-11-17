package com.edulibre.api.curso.service;

import com.edulibre.api.curso.dto.CursoDetalleResponse;
import com.edulibre.api.curso.dto.CursoResumenResponse;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CursoService {

    private final CursoRepository cursoRepository;
    private final InscripcionRepository inscripcionRepository;
    private final CurrentUserService currentUserService;

    @Transactional(readOnly = true)
    public List<CursoResumenResponse> listar(String categoriaSlug) {
        List<Curso> cursos = (categoriaSlug == null || categoriaSlug.isBlank())
                ? cursoRepository.findByPublicadoTrueOrderByCreadoEnDesc()
                : cursoRepository.findByPublicadoTrueAndCategoriaSlugOrderByCreadoEnDesc(categoriaSlug);

        return cursos.stream().map(this::toResumen).toList();
    }

    @Transactional(readOnly = true)
    public CursoDetalleResponse detalle(String id) {
        Curso curso = cursoRepository.findByIdAndPublicadoTrue(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));

        String estadoInscripcion = currentUserService.getCurrentUser()
                .flatMap(usuario -> inscripcionRepository.findByUsuarioIdAndCursoId(usuario.getId(), id))
                .map(inscripcion -> inscripcion.getEstado().name())
                .orElse(null);

        return new CursoDetalleResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getCategoria().getNombre(),
                curso.getInstructor(),
                curso.getImagenUrl(),
                curso.getNivel(),
                duracion(curso),
                estadoInscripcion);
    }

    private CursoResumenResponse toResumen(Curso curso) {
        return new CursoResumenResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getCategoria().getNombre(),
                curso.getNivel(),
                duracion(curso),
                curso.getImagenUrl());
    }

    private String duracion(Curso curso) {
        return curso.getDuracionSemanas() == null
                ? null
                : curso.getDuracionSemanas() + " semanas";
    }
}
