package com.edulibre.api.interaccion.dto;

import java.time.LocalDateTime;

public record ResenaResponse(
        String id,
        String usuario,
        int calificacion,
        String comentario,
        LocalDateTime creadoEn
) {
}
