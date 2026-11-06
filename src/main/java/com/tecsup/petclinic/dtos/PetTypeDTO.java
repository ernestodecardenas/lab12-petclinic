package com.tecsup.petclinic.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Tipo de mascota
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PetTypeDTO {

	private Integer id;

	@NotBlank(message = "name is required")
	private String name;

	private String description;

	private Boolean active;

	private String sizeCategory;

	private Integer averageLifespan;

	private String careLevel;
}
