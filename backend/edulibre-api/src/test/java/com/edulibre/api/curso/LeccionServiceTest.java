package com.edulibre.api.curso;

import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.curso.service.LeccionService;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.util.EstadoInscripcion;
import com.edulibre.api.shared.util.Rol;
import com.edulibre.api.usuario.entity.Usuario;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeccionServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private LeccionService leccionService;

    private final Usuario usuario = Usuario.builder()
            .id("u-1")
            .rol(Rol.ESTUDIANTE)
            .build();

    private final Usuario admin = Usuario.builder()
            .id("admin-1")
            .rol(Rol.ADMIN)
            .build();

    private final Curso curso = Curso.builder()
            .id("curso-1")
            .titulo("Curso")
            .publicado(true)
            .build();

    private final Leccion leccion = Leccion.builder()
            .id("lec-1")
            .curso(curso)
            .titulo("Introducción")
            .orden(1)
            .youtubeId("abc123")
            .duracionMin(75)
            .build();

    @Test
    void listarSinInscripcionDevuelveTemarioSinYoutubeId() {
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(usuario));
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(false);
        when(leccionRepository.findByCursoIdOrderByOrdenAsc("curso-1")).thenReturn(List.of(leccion));

        List<LeccionResponse> lecciones = leccionService.listar("curso-1");

        assertEquals(1, lecciones.size());
        assertEquals("Introducción", lecciones.getFirst().titulo());
        assertNull(lecciones.getFirst().youtubeId());
    }

    @Test
    void listarAnonimaDevuelveTemarioSinYoutubeId() {
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.empty());
        when(leccionRepository.findByCursoIdOrderByOrdenAsc("curso-1")).thenReturn(List.of(leccion));

        List<LeccionResponse> lecciones = leccionService.listar("curso-1");

        assertEquals(1, lecciones.size());
        assertNull(lecciones.getFirst().youtubeId());
    }

    @Test
    void listarAdminRecibeYoutubeIdSinInscripcion() {
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(admin));
        when(leccionRepository.findByCursoIdOrderByOrdenAsc("curso-1")).thenReturn(List.of(leccion));

        List<LeccionResponse> lecciones = leccionService.listar("curso-1");

        assertEquals("abc123", lecciones.getFirst().youtubeId());
    }

    @Test
    void listarDevuelveLeccionesConDuracionFormateada() {
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(usuario));
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(true);
        when(leccionRepository.findByCursoIdOrderByOrdenAsc("curso-1")).thenReturn(List.of(leccion));

        List<LeccionResponse> lecciones = leccionService.listar("curso-1");

        assertEquals("1h 15 min", lecciones.getFirst().duracion());
        assertEquals("abc123", lecciones.getFirst().youtubeId());
    }
}
