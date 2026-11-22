package com.edulibre.api.curso;

import com.edulibre.api.curso.dto.CategoriaAdminRequest;
import com.edulibre.api.curso.dto.CategoriaResponse;
import com.edulibre.api.curso.entity.Categoria;
import com.edulibre.api.curso.repository.CategoriaRepository;
import com.edulibre.api.curso.service.CategoriaService;
import com.edulibre.api.shared.exception.ConflictException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void slugificarGeneraSlugValido() {
        assertEquals("ciencias-de-datos-avanzadas", categoriaService.slugificar("Ciencias de Datos Avanzadas"));
        assertEquals("diseno-ux-ui", categoriaService.slugificar("Diseño UX/UI"));
        assertEquals("ia-ml", categoriaService.slugificar("  IA & ML  "));
    }

    @Test
    void crearGeneraSlugDesdeNombre() {
        when(categoriaRepository.existsByNombreIgnoreCase("Robótica")).thenReturn(false);
        when(categoriaRepository.findBySlug("robotica")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> {
            Categoria categoria = inv.getArgument(0);
            return Categoria.builder().id("cat-1").nombre(categoria.getNombre()).slug(categoria.getSlug()).build();
        });

        CategoriaResponse resultado = categoriaService.crear(new CategoriaAdminRequest("Robótica", null));

        assertEquals("robotica", resultado.slug());
    }

    @Test
    void crearRespetaSlugEditado() {
        when(categoriaRepository.existsByNombreIgnoreCase("Videojuegos")).thenReturn(false);
        when(categoriaRepository.findBySlug("game-dev")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenAnswer(inv -> inv.getArgument(0));

        CategoriaResponse resultado = categoriaService.crear(new CategoriaAdminRequest("Videojuegos", "Game Dev!"));

        assertEquals("game-dev", resultado.slug());
    }

    @Test
    void crearRechazaNombreDuplicado() {
        when(categoriaRepository.existsByNombreIgnoreCase("Robótica")).thenReturn(true);

        assertThrows(ConflictException.class,
                () -> categoriaService.crear(new CategoriaAdminRequest("Robótica", null)));
    }

    @Test
    void crearRechazaSlugDuplicado() {
        when(categoriaRepository.existsByNombreIgnoreCase("Nuevo Marketing")).thenReturn(false);
        when(categoriaRepository.findBySlug("marketing-digital")).thenReturn(Optional.of(
                Categoria.builder().id("cat-9").nombre("Marketing Digital").slug("marketing-digital").build()));

        assertThrows(ConflictException.class,
                () -> categoriaService.crear(new CategoriaAdminRequest("Nuevo Marketing", "Marketing Digital")));
    }
}
