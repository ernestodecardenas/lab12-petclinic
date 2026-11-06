package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Relacion Veterinario - Especialidad (tabla vet_specialties)
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "vet_specialties")
@IdClass(VetSpecialtyId.class)
@Data
public class VetSpecialty {

	@Id
	@Column(name = "vet_id")
	private Integer vetId;

	@Id
	@Column(name = "specialty_id")
	private Integer specialtyId;

	@Column(name = "certification_date")
	private LocalDate certificationDate;

	@Column(name = "years_experience")
	private Integer yearsExperience;

	@Column(name = "is_primary")
	private Boolean primarySpecialty;

	@Column(name = "notes")
	private String notes;
}
