package com.edulibre.api.curso.controller;

import com.edulibre.api.curso.dto.CategoriaAdminRequest;
import com.edulibre.api.curso.dto.CategoriaResponse;
import com.edulibre.api.curso.service.CategoriaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/categorias")
@RequiredArgsConstructor
public class AdminCategoriaController {

    private final CategoriaService categoriaService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoriaResponse crear(@Valid @RequestBody CategoriaAdminRequest request) {
        return categoriaService.crear(request);
    }
}
