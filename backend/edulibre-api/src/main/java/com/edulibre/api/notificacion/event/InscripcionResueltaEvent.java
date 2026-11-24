package com.edulibre.api.notificacion.event;

import com.edulibre.api.shared.util.EstadoInscripcion;

public record InscripcionResueltaEvent(
        String email,
        String nombrePostulante,
        String cursoTitulo,
        EstadoInscripcion estado,
        String comentarioAdmin,
        String enlace
) {
}
