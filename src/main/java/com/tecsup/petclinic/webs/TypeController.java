package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.services.PetTypeService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Tipos de mascota
 */
@RestController
@Slf4j
public class TypeController {

	private final PetTypeService petTypeService;

	public TypeController(PetTypeService petTypeService) {
		this.petTypeService = petTypeService;
	}

	/**
	 * GET /types  o  GET /types?active=true
	 */
	@GetMapping(value = "/types")
	public ResponseEntity<List<PetTypeDTO>> findAllTypes(@RequestParam(required = false) Boolean active) {
		List<PetTypeDTO> types = (active == null) ? petTypeService.findAll() : petTypeService.findByActive(active);
		return ResponseEntity.ok(types);
	}

	@GetMapping(value = "/types/{id}")
	public ResponseEntity<PetTypeDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(petTypeService.findById(id));
		} catch (PetTypeNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Reporte: cantidad de mascotas por tipo (excluye tipos inactivos)
	 */
	@GetMapping(value = "/types/report/pet-count")
	public ResponseEntity<List<PetCountByTypeDTO>> petCountByType() {
		return ResponseEntity.ok(petTypeService.getPetCountByType());
	}

	@PostMapping(value = "/types")
	public ResponseEntity<PetTypeDTO> create(@Valid @RequestBody PetTypeDTO petTypeDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(petTypeService.create(petTypeDTO));
	}

	@PutMapping(value = "/types/{id}")
	public ResponseEntity<PetTypeDTO> update(@Valid @RequestBody PetTypeDTO petTypeDTO, @PathVariable Integer id) {
		try {
			return ResponseEntity.ok(petTypeService.update(id, petTypeDTO));
		} catch (PetTypeNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/types/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			petTypeService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (PetTypeNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
