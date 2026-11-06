package com.tecsup.petclinic.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de la asignacion Veterinario - Especialidad.
 * certificationDate en formato yyyy-MM-dd
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VetSpecialtyDTO {

	@NotNull(message = "vetId is required")
	private Integer vetId;

	@NotNull(message = "specialtyId is required")
	private Integer specialtyId;

	private String certificationDate;

	@PositiveOrZero
	private Integer yearsExperience;

	private Boolean primary;

	private String notes;
}
