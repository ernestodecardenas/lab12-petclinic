package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST de Visitas
 */
@RestController
@Slf4j
public class VisitController {

	private final VisitService visitService;

	public VisitController(VisitService visitService) {
		this.visitService = visitService;
	}

	/**
	 * GET /visits con filtros opcionales:
	 *   ?petId=7 | ?vetId=2 | ?from=2024-01-01&to=2024-12-31 | ?vetId=1&from=..&to=..
	 */
	@GetMapping(value = "/visits")
	public ResponseEntity<List<VisitDTO>> findAllVisits(
			@RequestParam(required = false) Integer petId,
			@RequestParam(required = false) Integer vetId,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {

		boolean range = from != null && to != null;
		List<VisitDTO> visits;
		if (vetId != null && range)
			visits = visitService.findByVetIdAndDateBetween(vetId, from, to);
		else if (range)
			visits = visitService.findByDateBetween(from, to);
		else if (petId != null)
			visits = visitService.findByPetId(petId);
		else if (vetId != null)
			visits = visitService.findByVetId(vetId);
		else
			visits = visitService.findAll();
		return ResponseEntity.ok(visits);
	}

	@GetMapping(value = "/visits/{id}")
	public ResponseEntity<VisitDTO> findById(@PathVariable Integer id) {
		try {
			return ResponseEntity.ok(visitService.findById(id));
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@PostMapping(value = "/visits")
	public ResponseEntity<VisitDTO> create(@Valid @RequestBody VisitDTO visitDTO) {
		return ResponseEntity.status(HttpStatus.CREATED).body(visitService.create(visitDTO));
	}

	@PutMapping(value = "/visits/{id}")
	public ResponseEntity<VisitDTO> update(@Valid @RequestBody VisitDTO visitDTO, @PathVariable Integer id) {
		try {
			return ResponseEntity.ok(visitService.update(id, visitDTO));
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	@DeleteMapping(value = "/visits/{id}")
	public ResponseEntity<String> delete(@PathVariable Integer id) {
		try {
			visitService.delete(id);
			return ResponseEntity.ok(" Delete ID :" + id);
		} catch (VisitNotFoundException e) {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * Costo total (BigDecimal) de las visitas de una mascota
	 */
	@GetMapping(value = "/visits/pet/{petId}/total-cost")
	public ResponseEntity<Map<String, Object>> totalCostByPet(@PathVariable Integer petId) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("petId", petId);
		body.put("totalCost", visitService.calculateTotalCostByPet(petId));
		return ResponseEntity.ok(body);
	}

	/**
	 * Numero de visitas de una mascota
	 */
	@GetMapping(value = "/visits/pet/{petId}/count")
	public ResponseEntity<Map<String, Object>> countByPet(@PathVariable Integer petId) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("petId", petId);
		body.put("count", visitService.countVisitsByPet(petId));
		return ResponseEntity.ok(body);
	}
}
