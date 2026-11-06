package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Especialidades
 */
@RestController
@Slf4j
public class SpecialtyController {

	private final SpecialtyService specialtyService;

	public SpecialtyController(SpecialtyService specialtyService) {
		this.specialtyService = specialtyService;
	}

	/**
	 * GET /specialties , /specialties?name=surgery , /specialties?office=Farewell
	 */
	@GetMapping(value = "/specialties")
	public ResponseEntity<List<SpecialtyDTO>> findAllSpecialties(@RequestParam(required = false) String name,
																 @RequestParam(required = false) String office) {
		if (name != null)
			return ResponseEntity.ok(specialtyService.findByName(name));
		if (office != null)
			return ResponseEntity.ok(specialtyService.findByOffice(office));
		return ResponseEntity.ok(specialtyService.findAll());
	}

	@GetMapping(value = "/specialties/{id}")
	public ResponseEntity<SpecialtyDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(specialtyService.findById(id));
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/specialties")
	public ResponseEntity<SpecialtyDTO> create(@Valid @RequestBody SpecialtyDTO specialtyDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(specialtyService.create(specialtyDTO));
	}

	@PutMapping(value = "/specialties/{id}")
	public ResponseEntity<SpecialtyDTO> update(@Valid @RequestBody SpecialtyDTO specialtyDTO, @PathVariable Integer id) {
		try {
			return ResponseEntity.ok(specialtyService.update(id, specialtyDTO));
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/specialties/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			specialtyService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (SpecialtyNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
