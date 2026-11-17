package com.edulibre.api.inscripcion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RechazoRequest(
        @NotBlank(message = "El comentario de rechazo es obligatorio")
        @Size(max = 500, message = "El comentario no puede superar 500 caracteres")
        String comentarioAdmin
) {
}
