package com.edulibre.api.curso.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaAdminRequest(
        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 80, message = "El nombre no puede superar 80 caracteres")
        String nombre,

        @Size(max = 80, message = "El slug no puede superar 80 caracteres")
        String slug
) {
    public String slugOrNull() {
        return slug == null || slug.isBlank() ? null : slug.trim();
    }
}
