package com.edulibre.api.curso.dto;

public record LeccionResponse(
        String id,
        String titulo,
        int orden,
        String youtubeId,
        Integer duracionMin,
        String duracion
) {
}
