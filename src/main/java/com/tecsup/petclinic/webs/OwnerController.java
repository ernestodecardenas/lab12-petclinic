package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST de Duenos
 */
@RestController
@Slf4j
public class OwnerController {

	private final OwnerService ownerService;

	public OwnerController(OwnerService ownerService) {
		this.ownerService = ownerService;
	}

	/**
	 * GET /owners  o  GET /owners?lastName=Davis
	 */
	@GetMapping(value = "/owners")
	public ResponseEntity<List<OwnerDTO>> findAllOwners(@RequestParam(required = false) String lastName) {
		List<OwnerDTO> owners = (lastName == null) ? ownerService.findAll() : ownerService.findByLastName(lastName);
		return ResponseEntity.ok(owners);
	}

	@GetMapping(value = "/owners/{id}")
	public ResponseEntity<OwnerDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(ownerService.findById(id));
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/owners")
	public ResponseEntity<OwnerDTO> create(@Valid @RequestBody OwnerDTO ownerDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(ownerService.create(ownerDTO));
	}

	@PutMapping(value = "/owners/{id}")
	public ResponseEntity<OwnerDTO> update(@Valid @RequestBody OwnerDTO ownerDTO, @PathVariable Integer id) {
		try {
			return ResponseEntity.ok(ownerService.update(id, ownerDTO));
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/owners/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			ownerService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (OwnerNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
