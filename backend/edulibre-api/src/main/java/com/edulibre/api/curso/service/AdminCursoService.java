package com.edulibre.api.curso.service;

import com.edulibre.api.curso.dto.CursoAdminDetalleResponse;
import com.edulibre.api.curso.dto.CursoAdminRequest;
import com.edulibre.api.curso.dto.CursoAdminResponse;
import com.edulibre.api.curso.dto.LeccionRequest;
import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.entity.Categoria;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.CategoriaRepository;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.projection.CursoConteo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminCursoService {

    private final CursoRepository cursoRepository;
    private final LeccionRepository leccionRepository;
    private final CategoriaRepository categoriaRepository;
    private final InscripcionRepository inscripcionRepository;

    @Transactional(readOnly = true)
    public List<CursoAdminResponse> listar() {
        Map<String, Long> totalLecciones = conteoPorCurso(leccionRepository.countByCursoGrouped());
        Map<String, Long> totalInscritos = conteoPorCurso(inscripcionRepository.countByCursoGrouped());

        return cursoRepository.findAllByOrderByCreadoEnDesc().stream()
                .map(curso -> toResponse(
                        curso,
                        totalLecciones.getOrDefault(curso.getId(), 0L),
                        totalInscritos.getOrDefault(curso.getId(), 0L)))
                .toList();
    }

    private Map<String, Long> conteoPorCurso(List<CursoConteo> conteos) {
        return conteos.stream()
                .collect(Collectors.toMap(CursoConteo::getCursoId, CursoConteo::getTotal));
    }

    @Transactional(readOnly = true)
    public CursoAdminDetalleResponse detalle(String id) {
        Curso curso = buscarCurso(id);
        List<LeccionResponse> lecciones = leccionRepository.findByCursoIdOrderByOrdenAsc(curso.getId()).stream()
                .map(this::toLeccionResponse)
                .toList();

        return new CursoAdminDetalleResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getCategoria().getNombre(),
                curso.getCategoria().getSlug(),
                curso.getInstructor(),
                curso.getImagenUrl(),
                curso.getNivel(),
                curso.getDuracionSemanas(),
                curso.isPublicado(),
                curso.getCreadoEn(),
                lecciones.size(),
                inscripcionRepository.countByCursoId(curso.getId()),
                lecciones);
    }

    @Transactional
    public CursoAdminDetalleResponse crear(CursoAdminRequest request) {
        Categoria categoria = buscarCategoria(request.categoriaSlug());

        Curso curso = Curso.builder()
                .titulo(request.titulo().trim())
                .descripcion(request.descripcion())
                .categoria(categoria)
                .instructor(request.instructor().trim())
                .imagenUrl(request.imagenUrl())
                .nivel(request.nivel())
                .duracionSemanas(request.duracionSemanas())
                .publicado(request.publicadoOrDefault())
                .build();

        return toDetalleResponse(cursoRepository.save(curso));
    }

    @Transactional
    public CursoAdminDetalleResponse actualizar(String id, CursoAdminRequest request) {
        Curso curso = buscarCurso(id);
        Categoria categoria = buscarCategoria(request.categoriaSlug());

        curso.setTitulo(request.titulo().trim());
        curso.setDescripcion(request.descripcion());
        curso.setCategoria(categoria);
        curso.setInstructor(request.instructor().trim());
        curso.setImagenUrl(request.imagenUrl());
        curso.setNivel(request.nivel());
        curso.setDuracionSemanas(request.duracionSemanas());
        if (request.publicado() != null) {
            curso.setPublicado(request.publicado());
        }

        return toDetalleResponse(cursoRepository.save(curso));
    }

    @Transactional
    public CursoAdminResponse cambiarPublicado(String id, boolean publicado) {
        Curso curso = buscarCurso(id);
        curso.setPublicado(publicado);
        Curso guardado = cursoRepository.save(curso);
        return toResponse(guardado,
                leccionRepository.countByCursoId(guardado.getId()),
                inscripcionRepository.countByCursoId(guardado.getId()));
    }

    @Transactional(readOnly = true)
    public List<LeccionResponse> listarLecciones(String cursoId) {
        buscarCurso(cursoId);
        return leccionRepository.findByCursoIdOrderByOrdenAsc(cursoId).stream()
                .map(this::toLeccionResponse)
                .toList();
    }

    @Transactional
    public LeccionResponse crearLeccion(String cursoId, LeccionRequest request) {
        Curso curso = buscarCurso(cursoId);
        int orden = request.orden() != null ? request.orden() : leccionRepository.maxOrdenByCursoId(cursoId) + 1;

        if (leccionRepository.existsByCursoIdAndOrden(cursoId, orden)) {
            throw new ConflictException("Ya existe una lección con el orden " + orden + " en este curso");
        }

        Leccion leccion = Leccion.builder()
                .curso(curso)
                .titulo(request.titulo().trim())
                .orden(orden)
                .youtubeId(extraerYoutubeId(request.youtubeId()))
                .duracionMin(request.duracionMin())
                .build();

        return toLeccionResponse(leccionRepository.save(leccion));
    }

    @Transactional
    public LeccionResponse actualizarLeccion(String leccionId, LeccionRequest request) {
        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Lección no encontrada"));

        int orden = request.orden() != null ? request.orden() : leccion.getOrden();
        if (leccionRepository.existsByCursoIdAndOrdenAndIdNot(leccion.getCurso().getId(), orden, leccionId)) {
            throw new ConflictException("Ya existe una lección con el orden " + orden + " en este curso");
        }

        leccion.setTitulo(request.titulo().trim());
        leccion.setOrden(orden);
        leccion.setYoutubeId(extraerYoutubeId(request.youtubeId()));
        leccion.setDuracionMin(request.duracionMin());

        return toLeccionResponse(leccionRepository.save(leccion));
    }

    @Transactional
    public void eliminarLeccion(String leccionId) {
        Leccion leccion = leccionRepository.findById(leccionId)
                .orElseThrow(() -> new ResourceNotFoundException("Lección no encontrada"));
        leccionRepository.delete(leccion);
    }

    private Curso buscarCurso(String id) {
        return cursoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Curso no encontrado"));
    }

    private Categoria buscarCategoria(String slug) {
        return categoriaRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada: " + slug));
    }

    private String extraerYoutubeId(String valor) {
        String limpio = valor == null ? "" : valor.trim();
        int indice = limpio.indexOf("v=");
        if (indice >= 0) {
            limpio = limpio.substring(indice + 2);
            int separador = limpio.indexOf('&');
            if (separador >= 0) {
                limpio = limpio.substring(0, separador);
            }
            return limpio;
        }

        int ultimoSegmento = limpio.lastIndexOf('/');
        if (ultimoSegmento >= 0 && ultimoSegmento < limpio.length() - 1) {
            return limpio.substring(ultimoSegmento + 1);
        }

        return limpio;
    }

    private CursoAdminResponse toResponse(Curso curso, long totalLecciones, long totalInscritos) {
        return new CursoAdminResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getCategoria().getNombre(),
                curso.getCategoria().getSlug(),
                curso.getInstructor(),
                curso.getImagenUrl(),
                curso.getNivel(),
                curso.getDuracionSemanas(),
                curso.isPublicado(),
                curso.getCreadoEn(),
                totalLecciones,
                totalInscritos);
    }

    private CursoAdminDetalleResponse toDetalleResponse(Curso curso) {
        List<LeccionResponse> lecciones = leccionRepository.findByCursoIdOrderByOrdenAsc(curso.getId()).stream()
                .map(this::toLeccionResponse)
                .toList();

        return new CursoAdminDetalleResponse(
                curso.getId(),
                curso.getTitulo(),
                curso.getDescripcion(),
                curso.getCategoria().getNombre(),
                curso.getCategoria().getSlug(),
                curso.getInstructor(),
                curso.getImagenUrl(),
                curso.getNivel(),
                curso.getDuracionSemanas(),
                curso.isPublicado(),
                curso.getCreadoEn() != null ? curso.getCreadoEn() : LocalDateTime.now(),
                lecciones.size(),
                inscripcionRepository.countByCursoId(curso.getId()),
                lecciones);
    }

    private LeccionResponse toLeccionResponse(Leccion leccion) {
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
