package com.edulibre.api.curso.dto;

import java.time.LocalDateTime;
import java.util.List;

public record CursoAdminDetalleResponse(
        String id,
        String titulo,
        String descripcion,
        String categoria,
        String categoriaSlug,
        String instructor,
        String imagenUrl,
        String nivel,
        Integer duracionSemanas,
        boolean publicado,
        LocalDateTime creadoEn,
        long totalLecciones,
        long totalInscritos,
        List<LeccionResponse> lecciones
) {
}
