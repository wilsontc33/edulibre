package com.edulibre.api.curso;

import com.edulibre.api.curso.entity.Categoria;
import com.edulibre.api.curso.entity.Curso;
import com.edulibre.api.curso.entity.Leccion;
import com.edulibre.api.curso.repository.CategoriaRepository;
import com.edulibre.api.curso.repository.CursoRepository;
import com.edulibre.api.curso.repository.LeccionRepository;
import com.edulibre.api.inscripcion.entity.Inscripcion;
import com.edulibre.api.inscripcion.repository.InscripcionRepository;
import com.edulibre.api.shared.projection.CursoConteo;
import com.edulibre.api.shared.util.Rol;
import com.edulibre.api.usuario.entity.Usuario;
import com.edulibre.api.usuario.repository.UsuarioRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class CursoConteoProjectionTest {

    @Autowired
    private CategoriaRepository categoriaRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private LeccionRepository leccionRepository;

    @Autowired
    private InscripcionRepository inscripcionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Test
    void agrupaConteosPorCurso() {
        String sufijo = UUID.randomUUID().toString();

        Categoria categoria = categoriaRepository.save(Categoria.builder()
                .nombre("Categoria " + sufijo)
                .slug("categoria-" + sufijo)
                .build());

        Curso curso = cursoRepository.save(Curso.builder()
                .titulo("Curso")
                .categoria(categoria)
                .instructor("Instructor")
                .build());

        leccionRepository.save(Leccion.builder()
                .curso(curso).titulo("Leccion 1").orden(1).youtubeId("a").build());
        leccionRepository.save(Leccion.builder()
                .curso(curso).titulo("Leccion 2").orden(2).youtubeId("b").build());

        Usuario usuario = usuarioRepository.save(Usuario.builder()
                .nombre("Usuario")
                .email(sufijo + "@edulibre.com")
                .password("secreto123")
                .rol(Rol.ESTUDIANTE)
                .build());

        inscripcionRepository.save(Inscripcion.builder()
                .usuario(usuario)
                .curso(curso)
                .motivacion("Motivacion")
                .nivelPrevio("BASICO")
                .horasSemana(2)
                .build());

        assertEquals(2L, conteos(leccionRepository.countByCursoGrouped()).get(curso.getId()));
        assertEquals(1L, conteos(inscripcionRepository.countByCursoGrouped()).get(curso.getId()));
    }

    private static Map<String, Long> conteos(List<CursoConteo> conteos) {
        return conteos.stream()
                .collect(Collectors.toMap(CursoConteo::getCursoId, CursoConteo::getTotal));
    }
}
