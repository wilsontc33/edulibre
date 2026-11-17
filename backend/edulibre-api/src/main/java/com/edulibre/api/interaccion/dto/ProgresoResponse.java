package com.edulibre.api.interaccion.dto;

import java.util.List;

public record ProgresoResponse(
        String cursoId,
        long totalLecciones,
        long leccionesCompletadas,
        int porcentaje,
        List<String> leccionesCompletadasIds
) {
}
