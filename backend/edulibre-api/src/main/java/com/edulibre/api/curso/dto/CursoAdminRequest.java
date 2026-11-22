package com.edulibre.api.curso.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CursoAdminRequest(
        @NotBlank(message = "El título es obligatorio")
        @Size(max = 150, message = "El título no puede superar 150 caracteres")
        String titulo,

        String descripcion,

        @NotBlank(message = "La categoría es obligatoria")
        String categoriaSlug,

        @NotBlank(message = "El instructor es obligatorio")
        @Size(max = 120, message = "El instructor no puede superar 120 caracteres")
        String instructor,

        @Size(max = 500, message = "La URL de la imagen no puede superar 500 caracteres")
        String imagenUrl,

        @Size(max = 20, message = "El nivel no puede superar 20 caracteres")
        String nivel,

        @Min(value = 1, message = "La duración mínima es 1 semana")
        Integer duracionSemanas,

        Boolean publicado
) {
    public boolean publicadoOrDefault() {
        return publicado == null || publicado;
    }
}
