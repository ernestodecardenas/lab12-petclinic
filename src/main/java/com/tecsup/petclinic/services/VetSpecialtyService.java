package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;

import java.util.List;

public interface VetSpecialtyService {

	VetSpecialtyDTO assign(VetSpecialtyDTO dto)
			throws VetNotFoundException, SpecialtyNotFoundException, VetSpecialtyAlreadyExistsException;

	void remove(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	List<VetSpecialtyDTO> findAll();

	List<VetSpecialtyDTO> findByVetId(Integer vetId);

	List<SpecialtyDTO> findSpecialtiesByVet(Integer vetId) throws VetNotFoundException;

	List<VetDTO> findVetsBySpecialty(Integer specialtyId, Integer minExperience) throws SpecialtyNotFoundException;

	VetSpecialtyDTO setPrimary(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException;

	VetSpecialtyDTO updateYearsExperience(Integer vetId, Integer specialtyId, Integer years)
			throws VetSpecialtyNotFoundException;

	VetSpecialtyDTO updateCertificationDate(Integer vetId, Integer specialtyId, String certificationDate)
			throws VetSpecialtyNotFoundException;
}
