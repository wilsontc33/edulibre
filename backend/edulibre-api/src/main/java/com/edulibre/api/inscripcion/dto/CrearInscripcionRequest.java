package com.edulibre.api.inscripcion.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record CrearInscripcionRequest(
        @NotBlank(message = "El curso es obligatorio")
        String cursoId,

        @NotBlank(message = "La motivación es obligatoria")
        @Size(max = 1000, message = "La motivación no puede superar 1000 caracteres")
        String motivacion,

        @NotBlank(message = "El nivel previo es obligatorio")
        @Size(max = 20, message = "El nivel previo no puede superar 20 caracteres")
        String nivelPrevio,

        @NotNull(message = "Las horas semanales son obligatorias")
        @Min(value = 1, message = "Debes indicar al menos 1 hora semanal")
        @Max(value = 80, message = "Las horas semanales no pueden superar 80")
        Integer horasSemana
) {
}
