package com.edulibre.api.curso.controller;

import com.edulibre.api.curso.dto.CursoDetalleResponse;
import com.edulibre.api.curso.dto.CursoResumenResponse;
import com.edulibre.api.curso.service.CursoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping
    public List<CursoResumenResponse> listar(@RequestParam(required = false) String categoria) {
        return cursoService.listar(categoria);
    }

    @GetMapping("/{id}")
    public CursoDetalleResponse detalle(@PathVariable String id) {
        return cursoService.detalle(id);
    }
}
