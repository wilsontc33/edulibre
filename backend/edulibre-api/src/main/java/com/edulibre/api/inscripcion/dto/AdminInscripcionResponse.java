package com.edulibre.api.inscripcion.dto;

import java.time.LocalDateTime;

public record AdminInscripcionResponse(
        String id,
        String nombre,
        String email,
        String cursoId,
        String curso,
        String nivel,
        String motivacion,
        String estado,
        String comentarioAdmin,
        LocalDateTime creadoEn,
        LocalDateTime resueltoEn
) {
}
