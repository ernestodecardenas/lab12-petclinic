package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.repositories.VetRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class VetServiceImpl implements VetService {

	private final VetRepository vetRepository;
	private final VetMapper vetMapper;

	public VetServiceImpl(VetRepository vetRepository, VetMapper vetMapper) {
		this.vetRepository = vetRepository;
		this.vetMapper = vetMapper;
	}

	@Override
	public VetDTO create(VetDTO vetDTO) {
		Vet vet = vetMapper.mapToEntity(vetDTO);
		vet.setId(null);
		if (vet.getActive() == null)
			vet.setActive(true);
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public VetDTO update(Integer id, VetDTO vetDTO) throws VetNotFoundException {
		Vet vet = getEntity(id);
		vet.setFirstName(vetDTO.getFirstName());
		vet.setLastName(vetDTO.getLastName());
		vet.setEmail(vetDTO.getEmail());
		vet.setPhone(vetDTO.getPhone());
		if (vetDTO.getActive() != null)
			vet.setActive(vetDTO.getActive());
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	@Override
	public void delete(Integer id) throws VetNotFoundException {
		vetRepository.delete(getEntity(id));
	}

	@Override
	public VetDTO findById(Integer id) throws VetNotFoundException {
		return vetMapper.mapToDto(getEntity(id));
	}

	@Override
	public List<VetDTO> findAll() {
		return vetMapper.mapToDtoList(vetRepository.findAll());
	}

	@Override
	public List<VetDTO> findByActive(Boolean active) {
		return vetMapper.mapToDtoList(vetRepository.findByActive(active));
	}

	@Override
	public VetDTO changeActive(Integer id, boolean active) throws VetNotFoundException {
		Vet vet = getEntity(id);
		vet.setActive(active);
		return vetMapper.mapToDto(vetRepository.save(vet));
	}

	private Vet getEntity(Integer id) throws VetNotFoundException {
		return vetRepository.findById(id)
				.orElseThrow(() -> new VetNotFoundException("Record not found...!"));
	}
}
