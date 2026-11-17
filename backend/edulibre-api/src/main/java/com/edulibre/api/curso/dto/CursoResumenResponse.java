package com.edulibre.api.curso.dto;

public record CursoResumenResponse(
        String id,
        String titulo,
        String descripcion,
        String categoria,
        String nivel,
        String duracion,
        String imagenUrl
) {
}
