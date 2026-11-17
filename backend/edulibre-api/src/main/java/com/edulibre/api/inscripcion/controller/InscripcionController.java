package com.edulibre.api.inscripcion.controller;

import com.edulibre.api.inscripcion.dto.CrearInscripcionRequest;
import com.edulibre.api.inscripcion.dto.InscripcionResponse;
import com.edulibre.api.inscripcion.service.InscripcionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/inscripciones")
@RequiredArgsConstructor
public class InscripcionController {

    private final InscripcionService inscripcionService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InscripcionResponse crear(@Valid @RequestBody CrearInscripcionRequest request) {
        return inscripcionService.crear(request);
    }

    @GetMapping("/mis")
    public List<InscripcionResponse> mis() {
        return inscripcionService.mis();
    }
}
