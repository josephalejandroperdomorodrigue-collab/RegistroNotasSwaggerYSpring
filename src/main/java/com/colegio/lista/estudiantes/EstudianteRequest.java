package com.colegio.lista.estudiantes;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

public record EstudianteRequest(
		@JsonProperty("ID") @NotBlank @Size(max = 40) String idEstudiante,
		@NotBlank @Size(max = 120) String estudiante,
		@NotBlank @Email @Size(max = 160) String correoElectronico,
		@NotBlank @Size(max = 30) String grado,
		@NotBlank @Size(max = 120) String profesor,
		@NotBlank @Size(max = 120) String materia,
		@NotEmpty List<@NotNull @DecimalMin("0.0") @DecimalMax("5.0") BigDecimal> notas,
		boolean comelon,
		boolean canson,
		boolean dormilon,
		boolean ayudador,
		boolean comparteComida) {
}
