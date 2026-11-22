package com.edulibre.api.curso;

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
import com.edulibre.api.curso.service.AdminCursoService;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.shared.exception.ConflictException;
import com.edulibre.api.shared.exception.ResourceNotFoundException;
import com.edulibre.api.shared.projection.CursoConteo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminCursoServiceTest {

    @Mock
    private CursoRepository cursoRepository;

    @Mock
    private LeccionRepository leccionRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private InscripcionRepository inscripcionRepository;

    @InjectMocks
    private AdminCursoService adminCursoService;

    private final Categoria categoria = Categoria.builder()
            .id("cat-1")
            .nombre("Desarrollo Web")
            .slug("desarrollo-web")
            .build();

    private final Curso curso = Curso.builder()
            .id("curso-1")
            .titulo("Curso")
            .descripcion("Descripción")
            .categoria(categoria)
            .instructor("Instructor")
            .publicado(true)
            .creadoEn(LocalDateTime.now())
            .build();

    @Test
    void listarIncluyeConteos() {
        when(cursoRepository.findAllByOrderByCreadoEnDesc()).thenReturn(List.of(curso));
        when(leccionRepository.countByCursoGrouped()).thenReturn(List.of(conteo("curso-1", 5L)));
        when(inscripcionRepository.countByCursoGrouped()).thenReturn(List.of(conteo("curso-1", 12L)));

        List<CursoAdminResponse> resultado = adminCursoService.listar();

        assertEquals(1, resultado.size());
        assertEquals(5, resultado.get(0).totalLecciones());
        assertEquals(12, resultado.get(0).totalInscritos());
    }

    private static CursoConteo conteo(String cursoId, Long total) {
        return new CursoConteo() {
            @Override
            public String getCursoId() {
                return cursoId;
            }

            @Override
            public Long getTotal() {
                return total;
            }
        };
    }

    @Test
    void cambiarPublicadoOcultaCurso() {
        when(cursoRepository.findById("curso-1")).thenReturn(Optional.of(curso));
        when(cursoRepository.save(any(Curso.class))).thenAnswer(inv -> inv.getArgument(0));

        CursoAdminResponse resultado = adminCursoService.cambiarPublicado("curso-1", false);

        assertEquals(false, resultado.publicado());
    }

    @Test
    void crearFallaSiCategoriaNoExiste() {
        CursoAdminRequest request = new CursoAdminRequest(
                "Título", null, "no-existe", "Instructor", null, null, null, null);
        when(categoriaRepository.findBySlug("no-existe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminCursoService.crear(request));
    }

    @Test
    void crearLeccionAsignaOrdenAutoYEextraeYoutubeId() {
        when(cursoRepository.findById("curso-1")).thenReturn(Optional.of(curso));
        when(leccionRepository.maxOrdenByCursoId("curso-1")).thenReturn(2);
        when(leccionRepository.existsByCursoIdAndOrden("curso-1", 3)).thenReturn(false);
        when(leccionRepository.save(any(Leccion.class))).thenAnswer(inv -> {
            Leccion leccion = inv.getArgument(0);
            return Leccion.builder()
                    .id("lec-1")
                    .curso(leccion.getCurso())
                    .titulo(leccion.getTitulo())
                    .orden(leccion.getOrden())
                    .youtubeId(leccion.getYoutubeId())
                    .duracionMin(leccion.getDuracionMin())
                    .build();
        });

        LeccionResponse resultado = adminCursoService.crearLeccion("curso-1",
                new LeccionRequest("Video", null, "https://www.youtube.com/watch?v=abc123XYZ", 10));

        assertEquals(3, resultado.orden());
        assertEquals("abc123XYZ", resultado.youtubeId());
    }

    @Test
    void crearLeccionRechazaOrdenDuplicado() {
        when(cursoRepository.findById("curso-1")).thenReturn(Optional.of(curso));
        when(leccionRepository.existsByCursoIdAndOrden("curso-1", 1)).thenReturn(true);

        assertThrows(ConflictException.class, () -> adminCursoService.crearLeccion("curso-1",
                new LeccionRequest("Video", 1, "abc123", 10)));
    }

    @Test
    void eliminarLeccionFallaSiNoExiste() {
        when(leccionRepository.findById("no-existe")).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> adminCursoService.eliminarLeccion("no-existe"));
    }

    @Test
    void eliminarLeccionBorra() {
        Leccion leccion = Leccion.builder()
                .id("lec-1")
                .curso(curso)
                .titulo("Video")
                .orden(1)
                .youtubeId("abc")
                .build();
        when(leccionRepository.findById("lec-1")).thenReturn(Optional.of(leccion));

        adminCursoService.eliminarLeccion("lec-1");

        verify(leccionRepository).delete(leccion);
    }
}
