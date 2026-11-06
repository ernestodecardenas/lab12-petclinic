package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class SpecialtyServiceImpl implements SpecialtyService {

	private final SpecialtyRepository specialtyRepository;
	private final SpecialtyMapper specialtyMapper;

	public SpecialtyServiceImpl(SpecialtyRepository specialtyRepository, SpecialtyMapper specialtyMapper) {
		this.specialtyRepository = specialtyRepository;
		this.specialtyMapper = specialtyMapper;
	}

	@Override
	public SpecialtyDTO create(SpecialtyDTO specialtyDTO) {
		Specialty specialty = specialtyMapper.mapToEntity(specialtyDTO);
		specialty.setId(null);
		return specialtyMapper.mapToDto(specialtyRepository.save(specialty));
	}

	@Override
	public SpecialtyDTO update(Integer id, SpecialtyDTO specialtyDTO) throws SpecialtyNotFoundException {
		Specialty specialty = getEntity(id);
		specialty.setName(specialtyDTO.getName());
		specialty.setOffice(specialtyDTO.getOffice());
		specialty.setOpenHour(specialtyDTO.getOpenHour());
		specialty.setCloseHour(specialtyDTO.getCloseHour());
		return specialtyMapper.mapToDto(specialtyRepository.save(specialty));
	}

	@Override
	public void delete(Integer id) throws SpecialtyNotFoundException {
		specialtyRepository.delete(getEntity(id));
	}

	@Override
	public SpecialtyDTO findById(Integer id) throws SpecialtyNotFoundException {
		return specialtyMapper.mapToDto(getEntity(id));
	}

	@Override
	public List<SpecialtyDTO> findAll() {
		return specialtyMapper.mapToDtoList(specialtyRepository.findAll());
	}

	@Override
	public List<SpecialtyDTO> findByName(String name) {
		return specialtyMapper.mapToDtoList(specialtyRepository.findByName(name));
	}

	@Override
	public List<SpecialtyDTO> findByOffice(String office) {
		return specialtyMapper.mapToDtoList(specialtyRepository.findByOffice(office));
	}

	private Specialty getEntity(Integer id) throws SpecialtyNotFoundException {
		return specialtyRepository.findById(id)
				.orElseThrow(() -> new SpecialtyNotFoundException("Record not found...!"));
	}
}
