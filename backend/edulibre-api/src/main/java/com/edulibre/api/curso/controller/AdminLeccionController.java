package com.edulibre.api.curso.controller;

import com.edulibre.api.curso.dto.LeccionRequest;
import com.edulibre.api.curso.dto.LeccionResponse;
import com.edulibre.api.curso.service.AdminCursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminLeccionController {

    private final AdminCursoService adminCursoService;

    @GetMapping("/cursos/{cursoId}/lecciones")
    public List<LeccionResponse> listar(@PathVariable String cursoId) {
        return adminCursoService.listarLecciones(cursoId);
    }

    @PostMapping("/cursos/{cursoId}/lecciones")
    @ResponseStatus(HttpStatus.CREATED)
    public LeccionResponse crear(@PathVariable String cursoId,
                                 @Valid @RequestBody LeccionRequest request) {
        return adminCursoService.crearLeccion(cursoId, request);
    }

    @PutMapping("/lecciones/{id}")
    public LeccionResponse actualizar(@PathVariable String id,
                                      @Valid @RequestBody LeccionRequest request) {
        return adminCursoService.actualizarLeccion(id, request);
    }

    @DeleteMapping("/lecciones/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable String id) {
        adminCursoService.eliminarLeccion(id);
    }
}
