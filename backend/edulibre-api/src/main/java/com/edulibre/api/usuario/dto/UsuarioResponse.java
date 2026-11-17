package com.edulibre.api.usuario.dto;

import java.time.LocalDateTime;

public record UsuarioResponse(
        String id,
        String nombre,
        String email,
        String rol,
        LocalDateTime creadoEn
) {
}
