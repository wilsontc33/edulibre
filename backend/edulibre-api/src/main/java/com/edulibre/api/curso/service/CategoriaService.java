package com.edulibre.api.curso.service;

import com.edulibre.api.curso.dto.CategoriaAdminRequest;
import com.edulibre.api.curso.dto.CategoriaResponse;
import com.edulibre.api.curso.entity.Categoria;
import com.edulibre.api.curso.repository.CategoriaRepository;
import com.edulibre.api.shared.exception.ConflictException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
public class CategoriaService {

    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<CategoriaResponse> listar() {
        return categoriaRepository.findAllByOrderByNombreAsc().stream()
                .map(categoria -> new CategoriaResponse(
                        categoria.getId(),
                        categoria.getNombre(),
                        categoria.getSlug()))
                .toList();
    }

    @Transactional
    public CategoriaResponse crear(CategoriaAdminRequest request) {
        String nombre = request.nombre().trim();
        String slug = request.slugOrNull() != null
                ? slugificar(request.slugOrNull())
                : slugificar(nombre);

        if (categoriaRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe una categoría con el nombre \"" + nombre + "\"");
        }
        if (categoriaRepository.findBySlug(slug).isPresent()) {
            throw new ConflictException("Ya existe una categoría con el slug \"" + slug + "\"");
        }

        Categoria categoria = categoriaRepository.save(
                Categoria.builder()
                        .nombre(nombre)
                        .slug(slug)
                        .build());

        return new CategoriaResponse(categoria.getId(), categoria.getNombre(), categoria.getSlug());
    }

    public String slugificar(String valor) {
        String sinAcentos = Normalizer.normalize(valor, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return sinAcentos.toLowerCase(Locale.ROOT)
                .replaceAll("[^a-z0-9]+", "-")
                .replaceAll("(^-+|-+$)", "")
                .replaceAll("-{2,}", "-");
    }
}
