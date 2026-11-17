package com.edulibre.api.inscripcion.dto;

import java.time.LocalDateTime;

public record InscripcionResponse(
        String id,
        String cursoId,
        String curso,
        String estado,
        String motivoRechazo,
        LocalDateTime creadoEn,
        LocalDateTime resueltoEn
) {
}
