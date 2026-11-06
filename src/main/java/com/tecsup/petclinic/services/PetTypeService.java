package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;

import java.util.List;

public interface PetTypeService {

	PetTypeDTO create(PetTypeDTO petTypeDTO);

	PetTypeDTO update(Integer id, PetTypeDTO petTypeDTO) throws PetTypeNotFoundException;

	void delete(Integer id) throws PetTypeNotFoundException;

	PetTypeDTO findById(Integer id) throws PetTypeNotFoundException;

	List<PetTypeDTO> findAll();

	List<PetTypeDTO> findByActive(Boolean active);

	List<PetCountByTypeDTO> getPetCountByType();
}
