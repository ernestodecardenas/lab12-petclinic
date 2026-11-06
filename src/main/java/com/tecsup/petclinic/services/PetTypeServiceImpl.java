package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.entities.PetType;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.mapper.PetTypeMapper;
import com.tecsup.petclinic.repositories.PetTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class PetTypeServiceImpl implements PetTypeService {

	private final PetTypeRepository petTypeRepository;
	private final PetTypeMapper petTypeMapper;

	public PetTypeServiceImpl(PetTypeRepository petTypeRepository, PetTypeMapper petTypeMapper) {
		this.petTypeRepository = petTypeRepository;
		this.petTypeMapper = petTypeMapper;
	}

	@Override
	public PetTypeDTO create(PetTypeDTO petTypeDTO) {
		PetType petType = petTypeMapper.mapToEntity(petTypeDTO);
		petType.setId(null);
		if (petType.getActive() == null)
			petType.setActive(true);
		return petTypeMapper.mapToDto(petTypeRepository.save(petType));
	}

	@Override
	public PetTypeDTO update(Integer id, PetTypeDTO dto) throws PetTypeNotFoundException {
		PetType petType = getEntity(id);
		petType.setName(dto.getName());
		petType.setDescription(dto.getDescription());
		if (dto.getActive() != null)
			petType.setActive(dto.getActive());
		petType.setSizeCategory(dto.getSizeCategory());
		petType.setAverageLifespan(dto.getAverageLifespan());
		petType.setCareLevel(dto.getCareLevel());
		return petTypeMapper.mapToDto(petTypeRepository.save(petType));
	}

	@Override
	public void delete(Integer id) throws PetTypeNotFoundException {
		petTypeRepository.delete(getEntity(id));
	}

	@Override
	public PetTypeDTO findById(Integer id) throws PetTypeNotFoundException {
		return petTypeMapper.mapToDto(getEntity(id));
	}

	@Override
	public List<PetTypeDTO> findAll() {
		return petTypeMapper.mapToDtoList(petTypeRepository.findAll());
	}

	@Override
	public List<PetTypeDTO> findByActive(Boolean active) {
		return petTypeMapper.mapToDtoList(petTypeRepository.findByActive(active));
	}

	@Override
	public List<PetCountByTypeDTO> getPetCountByType() {
		return petTypeRepository.countPetsByActiveType();
	}

	private PetType getEntity(Integer id) throws PetTypeNotFoundException {
		return petTypeRepository.findById(id)
				.orElseThrow(() -> new PetTypeNotFoundException("Record not found...!"));
	}
}
