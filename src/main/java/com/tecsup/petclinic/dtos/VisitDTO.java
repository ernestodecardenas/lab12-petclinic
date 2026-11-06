package com.tecsup.petclinic.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO de Visita. visitDate en formato yyyy-MM-dd
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class VisitDTO {

	private Integer id;

	@NotNull(message = "petId is required")
	private Integer petId;

	private Integer vetId;

	@NotBlank(message = "visitDate is required")
	private String visitDate;

	private String description;

	@PositiveOrZero
	private BigDecimal cost;
}
