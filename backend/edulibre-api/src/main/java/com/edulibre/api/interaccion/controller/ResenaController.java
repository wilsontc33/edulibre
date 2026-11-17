package com.edulibre.api.interaccion.controller;

import com.edulibre.api.interaccion.dto.ResenaRequest;
import com.edulibre.api.interaccion.dto.ResenaResponse;
import com.edulibre.api.interaccion.service.ResenaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @GetMapping("/api/cursos/{cursoId}/resenas")
    public List<ResenaResponse> listar(@PathVariable String cursoId) {
        return resenaService.listar(cursoId);
    }

    @PostMapping("/api/cursos/{cursoId}/resenas")
    @ResponseStatus(HttpStatus.CREATED)
    public ResenaResponse crear(@PathVariable String cursoId,
                                @Valid @RequestBody ResenaRequest request) {
        return resenaService.crear(cursoId, request);
    }
}
