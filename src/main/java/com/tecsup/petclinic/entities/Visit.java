package com.tecsup.petclinic.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 
 * @author jgomezm
 *
 */
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity(name = "visits")
@Data
public class Visit {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	@Column(name = "pet_id")
	private Integer petId;

	@Column(name = "vet_id")
	private Integer vetId;

	@Column(name = "visit_date")
	private LocalDate visitDate;

	@Column(name = "description")
	private String description;

	@Column(name = "cost")
	private BigDecimal cost;
}
