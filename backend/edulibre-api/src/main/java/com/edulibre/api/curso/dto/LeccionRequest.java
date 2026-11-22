package com.edulibre.api.curso.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LeccionRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede superar 150 caracteres")
        String titulo,

        @Min(value = 1, message = "El orden mínimo es 1")
        Integer orden,

        @NotBlank(message = "El enlace de YouTube es obligatorio")
        @Size(max = 120, message = "El enlace de YouTube no puede superar 120 caracteres")
        String youtubeId,

        @Min(value = 0, message = "La duración no puede ser negativa")
        Integer duracionMin
) {
}
