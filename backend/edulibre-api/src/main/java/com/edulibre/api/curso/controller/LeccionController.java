package com.edulibre.api.curso.controller;

import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.service.LeccionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cursos/{cursoId}/lecciones")
@RequiredArgsConstructor
public class LeccionController {

    private final LeccionService leccionService;

    @GetMapping
    public List<LeccionResponse> listar(@PathVariable String cursoId) {
        return leccionService.listar(cursoId);
    }
}
