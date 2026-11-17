package com.edulibre.api.interaccion;

import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.interaccion.dto.ProgresoResponse;
import com.edulibre.api.interaccion.repository.ProgresoLeccionRepository;
import com.edulibre.api.interaccion.service.ProgresoService;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProgresoServiceTest {

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private ProgresoLeccionRepository progresoLeccionRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @Mock
    private CurrentUserService currentUserService;

    @InjectMocks
    private ProgresoService progresoService;

    private final Usuario usuario = Usuario.builder()
            .id("u-1")
            .nombre("Ana")
            .email("ana@edulibre.com")
            .rol(Rol.ESTUDIANTE)
            .build();

    private final Curso curso = Curso.builder().id("curso-1").titulo("Curso").build();

    @Test
    void obtenerCalculaPorcentaje() {
        when(currentUserService.requireCurrentUser()).thenReturn(usuario);
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(true);
        when(leccionRepository.countByCursoId("curso-1")).thenReturn(4L);
        when(progresoLeccionRepository.findLeccionIdsByUsuarioIdAndCursoId("u-1", "curso-1"))
                .thenReturn(List.of("lec-1"));

        ProgresoResponse response = progresoService.obtener("curso-1");

        assertEquals(4L, response.totalLecciones());
        assertEquals(1L, response.leccionesCompletadas());
        assertEquals(25, response.porcentaje());
        assertEquals(List.of("lec-1"), response.leccionesCompletadasIds());
    }

    @Test
    void completarEsIdempotente() {
        Leccion leccion = Leccion.builder()
                .id("lec-1")
                .curso(curso)
                .titulo("Intro")
                .orden(1)
                .youtubeId("abc")
                .build();

        when(currentUserService.requireCurrentUser()).thenReturn(usuario);
        when(leccionRepository.findById("lec-1")).thenReturn(Optional.of(leccion));
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(true);
        when(progresoLeccionRepository.existsByUsuarioIdAndLeccionId("u-1", "lec-1")).thenReturn(true);
        when(leccionRepository.countByCursoId("curso-1")).thenReturn(2L);
        when(progresoLeccionRepository.findLeccionIdsByUsuarioIdAndCursoId("u-1", "curso-1"))
                .thenReturn(List.of("lec-1"));

        progresoService.completar("lec-1");

        verify(progresoLeccionRepository, never()).save(any());
    }

    @Test
    void obtenerRechazaSinInscripcionAprobada() {
        when(currentUserService.requireCurrentUser()).thenReturn(usuario);
        when(inscripcionRepository.existsByUsuarioIdAndCursoIdAndEstado(
                "u-1", "curso-1", EstadoInscripcion.APROBADA)).thenReturn(false);

        assertThrows(ForbiddenException.class, () -> progresoService.obtener("curso-1"));
    }
}
