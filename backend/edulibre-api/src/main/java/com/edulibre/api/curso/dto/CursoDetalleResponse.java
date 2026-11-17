package com.edulibre.api.curso.dto;

public record CursoDetalleResponse(
        String id,
        String titulo,
        String descripcion,
        String categoria,
        String instructor,
        String imagenUrl,
        String nivel,
        String duracion,
        String estadoInscripcion
) {
}
