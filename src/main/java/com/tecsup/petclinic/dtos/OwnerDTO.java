package com.tecsup.petclinic.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO de Dueno de mascota
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class OwnerDTO {

	private Integer id;

	private String firstName;

	@NotBlank(message = "lastName is required")
	private String lastName;

	private String address;

	private String city;

	private String telephone;

}
