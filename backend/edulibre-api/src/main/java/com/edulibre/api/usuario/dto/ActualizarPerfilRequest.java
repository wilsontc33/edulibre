package com.edulibre.api.usuario.dto;

import jakarta.validation.constraints.Size;

public record ActualizarPerfilRequest(
        @Size(min = 1, max = 120, message = "El nombre debe tener entre 1 y 120 caracteres")
        String nombre
) {
}
