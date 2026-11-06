package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface VisitService {

	VisitDTO create(VisitDTO visitDTO);

	VisitDTO update(Integer id, VisitDTO visitDTO) throws VisitNotFoundException;

	void delete(Integer id) throws VisitNotFoundException;

	VisitDTO findById(Integer id) throws VisitNotFoundException;

	List<VisitDTO> findAll();

	List<VisitDTO> findByPetId(Integer petId);

	List<VisitDTO> findByVetId(Integer vetId);

	List<VisitDTO> findByDateBetween(LocalDate from, LocalDate to);

	List<VisitDTO> findByVetIdAndDateBetween(Integer vetId, LocalDate from, LocalDate to);

	BigDecimal calculateTotalCostByPet(Integer petId);

	long countVisitsByPet(Integer petId);
}
