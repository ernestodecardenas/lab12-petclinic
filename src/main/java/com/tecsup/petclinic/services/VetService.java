package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;

import java.util.List;

public interface VetService {

	VetDTO create(VetDTO vetDTO);

	VetDTO update(Integer id, VetDTO vetDTO) throws VetNotFoundException;

	void delete(Integer id) throws VetNotFoundException;

	VetDTO findById(Integer id) throws VetNotFoundException;

	List<VetDTO> findAll();

	List<VetDTO> findByActive(Boolean active);

	VetDTO changeActive(Integer id, boolean active) throws VetNotFoundException;
}
