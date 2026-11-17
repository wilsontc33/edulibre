package com.edulibre.api.inscripcion.controller;

import com.edulibre.api.inscripcion.dto.AdminInscripcionResponse;
import com.edulibre.api.inscripcion.dto.RechazoRequest;
import com.edulibre.api.inscripcion.service.InscripcionService;
import com.edulibre.api.shared.util.EstadoInscripcion;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin/inscripciones")
@RequiredArgsConstructor
public class AdminInscripcionController {

    private final InscripcionService inscripcionService;

    @GetMapping
    public List<AdminInscripcionResponse> listar(@RequestParam(required = false) EstadoInscripcion estado) {
        return inscripcionService.listar(estado);
    }

    @PatchMapping("/{id}/aprobar")
    public AdminInscripcionResponse aprobar(@PathVariable String id) {
        return inscripcionService.aprobar(id);
    }

    @PatchMapping("/{id}/rechazar")
    public AdminInscripcionResponse rechazar(@PathVariable String id,
                                             @Valid @RequestBody RechazoRequest request) {
        return inscripcionService.rechazar(id, request.comentarioAdmin());
    }
}
