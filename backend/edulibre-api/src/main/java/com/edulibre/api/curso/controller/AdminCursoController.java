package com.edulibre.api.curso.controller;

import com.edulibre.api.curso.dto.CursoAdminDetalleResponse;
import com.edulibre.api.curso.dto.CursoAdminRequest;
import com.edulibre.api.curso.dto.CursoAdminResponse;
import com.edulibre.api.curso.dto.PublicadoRequest;
import com.edulibre.api.curso.service.AdminCursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/cursos")
@RequiredArgsConstructor
public class AdminCursoController {

    private final AdminCursoService adminCursoService;

    @GetMapping
    public List<CursoAdminResponse> listar() {
        return adminCursoService.listar();
    }

    @GetMapping("/{id}")
    public CursoAdminDetalleResponse detalle(@PathVariable String id) {
        return adminCursoService.detalle(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CursoAdminDetalleResponse crear(@Valid @RequestBody CursoAdminRequest request) {
        return adminCursoService.crear(request);
    }

    @PutMapping("/{id}")
    public CursoAdminDetalleResponse actualizar(@PathVariable String id,
                                                @Valid @RequestBody CursoAdminRequest request) {
        return adminCursoService.actualizar(id, request);
    }

    @PatchMapping("/{id}/publicado")
    public CursoAdminResponse cambiarPublicado(@PathVariable String id,
                                               @RequestBody PublicadoRequest request) {
        return adminCursoService.cambiarPublicado(id, request.publicado());
    }
}
