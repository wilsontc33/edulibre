package com.edulibre.api.curso;

import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.curso.service.LeccionService;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.security.service.CurrentUserService;
import com.edulibre.api.shared.exception.ForbiddenException;
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
import static org.junit.jupiter.api.Assertions.assertThrows;
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

    private final Curso curso = Curso.builder()
            .id("curso-1")
            .titulo("Curso")
            .publicado(true)
            .build();

    @Test
    void listarRechazaSinInscripcionAprobada() {
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(usuario));
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> leccionService.listar("curso-1"));
    }

    @Test
    void listarDevuelveLeccionesConDuracionFormateada() {
        when(cursoRepository.findByIdAndPublicadoTrue("curso-1")).thenReturn(Optional.of(curso));
        when(currentUserService.getCurrentUser()).thenReturn(Optional.of(usuario));
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(true);
        when(leccionRepository.findByCursoIdOrderByOrdenAsc("curso-1")).thenReturn(List.of(
                Leccion.builder()
                        .id("lec-1")
                        .curso(curso)
                        .titulo("Introducción")
                        .orden(1)
                        .youtubeId("abc")
                        .duracionMin(75)
                        .build()));

        List<LeccionResponse> lecciones = leccionService.listar("curso-1");

        assertEquals(1, lecciones.size());
        assertEquals("1h 15 min", lecciones.getFirst().duracion());
    }
}
