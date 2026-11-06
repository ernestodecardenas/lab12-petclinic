package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.services.VetSpecialtyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de la relacion Veterinario - Especialidad
 */
@RestController
@Slf4j
public class VetSpecialtyController {

	private final VetSpecialtyService vetSpecialtyService;

	public VetSpecialtyController(VetSpecialtyService vetSpecialtyService) {
		this.vetSpecialtyService = vetSpecialtyService;
	}

	/**
	 * GET /vet-specialties  o  GET /vet-specialties?vetId=3
	 */
	@GetMapping(value = "/vet-specialties")
	public ResponseEntity<List<VetSpecialtyDTO>> findAll(@RequestParam(required = false) Integer vetId) {
		return ResponseEntity.ok(vetId == null ? vetSpecialtyService.findAll() : vetSpecialtyService.findByVetId(vetId));
	}

	/**
	 * Asigna una especialidad a un veterinario. 409 si ya estaba asignada.
	 */
	@PostMapping(value = "/vet-specialties")
	public ResponseEntity<VetSpecialtyDTO> assign(@Valid @RequestBody VetSpecialtyDTO dto) {
		try {
			return ResponseEntity.status(HttpStatus.CREATED).body(vetSpecialtyService.assign(dto));
		} catch (VetNotFoundException | SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		} catch (VetSpecialtyAlreadyExistsException e) {
			return ResponseEntity.status(HttpStatus.CONFLICT).build();
		}
	}

	@DeleteMapping(value = "/vet-specialties/{vetId}/{specialtyId}")
	public ResponseEntity<String> remove(@PathVariable Integer vetId, @PathVariable Integer specialtyId) {
		try {
			vetSpecialtyService.remove(vetId, specialtyId);
			return ResponseEntity.ok(" Delete vet " + vetId + " - specialty " + specialtyId);
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@GetMapping(value = "/vets/{vetId}/specialties")
	public ResponseEntity<List<SpecialtyDTO>> findSpecialtiesByVet(@PathVariable Integer vetId) {
		try {
			return ResponseEntity.ok(vetSpecialtyService.findSpecialtiesByVet(vetId));
		} catch (VetNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * GET /specialties/{id}/vets  o  /specialties/{id}/vets?minExperience=7
	 */
	@GetMapping(value = "/specialties/{specialtyId}/vets")
	public ResponseEntity<List<VetDTO>> findVetsBySpecialty(@PathVariable Integer specialtyId,
															@RequestParam(required = false) Integer minExperience) {
		try {
			return ResponseEntity.ok(vetSpecialtyService.findVetsBySpecialty(specialtyId, minExperience));
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping(value = "/vet-specialties/{vetId}/{specialtyId}/primary")
	public ResponseEntity<VetSpecialtyDTO> setPrimary(@PathVariable Integer vetId, @PathVariable Integer specialtyId) {
		try {
			return ResponseEntity.ok(vetSpecialtyService.setPrimary(vetId, specialtyId));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Body: {"yearsExperience": 10}
	 */
	@PutMapping(value = "/vet-specialties/{vetId}/{specialtyId}/experience")
	public ResponseEntity<VetSpecialtyDTO> updateExperience(@PathVariable Integer vetId, @PathVariable Integer specialtyId,
															@RequestBody VetSpecialtyDTO body) {
		if (body.getYearsExperience() == null || body.getYearsExperience() < 0)
			return ResponseEntity.badRequest().build();
		try {
			return ResponseEntity.ok(vetSpecialtyService.updateYearsExperience(vetId, specialtyId, body.getYearsExperience()));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Body: {"certificationDate": "2020-05-10"}
	 */
	@PutMapping(value = "/vet-specialties/{vetId}/{specialtyId}/certification")
	public ResponseEntity<VetSpecialtyDTO> updateCertification(@PathVariable Integer vetId, @PathVariable Integer specialtyId,
															   @RequestBody VetSpecialtyDTO body) {
		if (body.getCertificationDate() == null || body.getCertificationDate().isBlank())
			return ResponseEntity.badRequest().build();
		try {
			return ResponseEntity.ok(vetSpecialtyService.updateCertificationDate(vetId, specialtyId, body.getCertificationDate()));
		} catch (VetSpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
