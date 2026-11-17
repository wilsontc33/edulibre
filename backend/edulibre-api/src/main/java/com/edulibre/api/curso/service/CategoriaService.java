package com.edulibre.api.curso.service;

import com.edulibre.api.curso.dto.CategoriaResponse;
import com.edulibre.api.curso.repository.CategoriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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
}
