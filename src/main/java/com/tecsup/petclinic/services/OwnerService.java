package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;

import java.util.List;

public interface OwnerService {

	OwnerDTO create(OwnerDTO ownerDTO);

	OwnerDTO update(Integer id, OwnerDTO ownerDTO) throws OwnerNotFoundException;

	void delete(Integer id) throws OwnerNotFoundException;

	OwnerDTO findById(Integer id) throws OwnerNotFoundException;

	List<OwnerDTO> findAll();

	List<OwnerDTO> findByLastName(String lastName);
}
