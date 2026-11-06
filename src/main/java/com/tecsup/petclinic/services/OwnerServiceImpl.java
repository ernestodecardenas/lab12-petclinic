package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.mapper.OwnerMapper;
import com.tecsup.petclinic.repositories.OwnerRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class OwnerServiceImpl implements OwnerService {

	private final OwnerRepository ownerRepository;
	private final OwnerMapper ownerMapper;

	public OwnerServiceImpl(OwnerRepository ownerRepository, OwnerMapper ownerMapper) {
		this.ownerRepository = ownerRepository;
		this.ownerMapper = ownerMapper;
	}

	@Override
	public OwnerDTO create(OwnerDTO ownerDTO) {
		Owner owner = ownerMapper.mapToEntity(ownerDTO);
		owner.setId(null);
		return ownerMapper.mapToDto(ownerRepository.save(owner));
	}

	@Override
	public OwnerDTO update(Integer id, OwnerDTO ownerDTO) throws OwnerNotFoundException {
		Owner owner = getEntity(id);
		owner.setFirstName(ownerDTO.getFirstName());
		owner.setLastName(ownerDTO.getLastName());
		owner.setAddress(ownerDTO.getAddress());
		owner.setCity(ownerDTO.getCity());
		owner.setTelephone(ownerDTO.getTelephone());
		return ownerMapper.mapToDto(ownerRepository.save(owner));
	}

	@Override
	public void delete(Integer id) throws OwnerNotFoundException {
		ownerRepository.delete(getEntity(id));
	}

	@Override
	public OwnerDTO findById(Integer id) throws OwnerNotFoundException {
		return ownerMapper.mapToDto(getEntity(id));
	}

	@Override
	public List<OwnerDTO> findAll() {
		return ownerMapper.mapToDtoList(ownerRepository.findAll());
	}

	@Override
	public List<OwnerDTO> findByLastName(String lastName) {
		return ownerMapper.mapToDtoList(ownerRepository.findByLastName(lastName));
	}

	private Owner getEntity(Integer id) throws OwnerNotFoundException {
		return ownerRepository.findById(id)
				.orElseThrow(() -> new OwnerNotFoundException("Record not found...!"));
	}
}
