package com.edulibre.api.interaccion.controller;

import com.edulibre.api.interaccion.dto.ProgresoResponse;
import com.edulibre.api.interaccion.service.ProgresoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProgresoController {

    private final ProgresoService progresoService;

    @PostMapping("/api/lecciones/{leccionId}/completar")
    public ProgresoResponse completar(@PathVariable String leccionId) {
        return progresoService.completar(leccionId);
    }

    @GetMapping("/api/cursos/{cursoId}/progreso")
    public ProgresoResponse obtener(@PathVariable String cursoId) {
        return progresoService.obtener(cursoId);
    }
}
