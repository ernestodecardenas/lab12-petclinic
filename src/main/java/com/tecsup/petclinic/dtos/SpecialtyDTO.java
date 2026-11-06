package com.tecsup.petclinic.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Especialidad.
 * openHour / closeHour corresponden a las columnas h_open / h_close.
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class SpecialtyDTO {

	private Integer id;

	@NotBlank(message = "name is required")
	private String name;

	private String office;

	@NotNull
	@Min(value = 0, message = "openHour must be between 0 and 23")
	@Max(value = 23, message = "openHour must be between 0 and 23")
	private Integer openHour;

	@NotNull
	@Min(value = 0, message = "closeHour must be between 0 and 23")
	@Max(value = 23, message = "closeHour must be between 0 and 23")
	private Integer closeHour;

	/**
	 * Regla de negocio: la hora de apertura debe ser menor a la de cierre
	 */
	@JsonIgnore
	@AssertTrue(message = "openHour must be lower than closeHour")
	public boolean isValidSchedule() {
		if (openHour == null || closeHour == null)
			return true; // lo valida @NotNull
		return openHour < closeHour;
	}
}
