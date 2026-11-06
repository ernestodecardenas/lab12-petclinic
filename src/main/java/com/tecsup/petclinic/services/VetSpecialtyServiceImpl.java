package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.VetSpecialty;
import com.tecsup.petclinic.entities.VetSpecialtyId;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.mapper.SpecialtyMapper;
import com.tecsup.petclinic.mapper.VetMapper;
import com.tecsup.petclinic.mapper.VetSpecialtyMapper;
import com.tecsup.petclinic.repositories.SpecialtyRepository;
import com.tecsup.petclinic.repositories.VetRepository;
import com.tecsup.petclinic.repositories.VetSpecialtyRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class VetSpecialtyServiceImpl implements VetSpecialtyService {

	private final VetSpecialtyRepository vetSpecialtyRepository;
	private final VetRepository vetRepository;
	private final SpecialtyRepository specialtyRepository;
	private final VetSpecialtyMapper vetSpecialtyMapper;
	private final VetMapper vetMapper;
	private final SpecialtyMapper specialtyMapper;

	public VetSpecialtyServiceImpl(VetSpecialtyRepository vetSpecialtyRepository, VetRepository vetRepository,
								   SpecialtyRepository specialtyRepository, VetSpecialtyMapper vetSpecialtyMapper,
								   VetMapper vetMapper, SpecialtyMapper specialtyMapper) {
		this.vetSpecialtyRepository = vetSpecialtyRepository;
		this.vetRepository = vetRepository;
		this.specialtyRepository = specialtyRepository;
		this.vetSpecialtyMapper = vetSpecialtyMapper;
		this.vetMapper = vetMapper;
		this.specialtyMapper = specialtyMapper;
	}

	@Override
	public VetSpecialtyDTO assign(VetSpecialtyDTO dto)
			throws VetNotFoundException, SpecialtyNotFoundException, VetSpecialtyAlreadyExistsException {

		if (!vetRepository.existsById(dto.getVetId()))
			throw new VetNotFoundException("Vet not found...!");
		if (!specialtyRepository.existsById(dto.getSpecialtyId()))
			throw new SpecialtyNotFoundException("Specialty not found...!");
		if (vetSpecialtyRepository.existsById(new VetSpecialtyId(dto.getVetId(), dto.getSpecialtyId())))
			throw new VetSpecialtyAlreadyExistsException("Specialty already assigned to vet...!");

		VetSpecialty entity = vetSpecialtyMapper.mapToEntity(dto);
		if (entity.getYearsExperience() == null)
			entity.setYearsExperience(0);
		if (entity.getPrimarySpecialty() == null)
			entity.setPrimarySpecialty(false);
		return vetSpecialtyMapper.mapToDto(vetSpecialtyRepository.save(entity));
	}

	@Override
	public void remove(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		vetSpecialtyRepository.delete(getEntity(vetId, specialtyId));
	}

	@Override
	public List<VetSpecialtyDTO> findAll() {
		return vetSpecialtyMapper.mapToDtoList(vetSpecialtyRepository.findAll());
	}

	@Override
	public List<VetSpecialtyDTO> findByVetId(Integer vetId) {
		return vetSpecialtyMapper.mapToDtoList(vetSpecialtyRepository.findByVetId(vetId));
	}

	@Override
	public List<SpecialtyDTO> findSpecialtiesByVet(Integer vetId) throws VetNotFoundException {
		if (!vetRepository.existsById(vetId))
			throw new VetNotFoundException("Vet not found...!");
		List<Integer> ids = vetSpecialtyRepository.findByVetId(vetId).stream()
				.map(VetSpecialty::getSpecialtyId).toList();
		return specialtyMapper.mapToDtoList(specialtyRepository.findAllById(ids));
	}

	@Override
	public List<VetDTO> findVetsBySpecialty(Integer specialtyId, Integer minExperience)
			throws SpecialtyNotFoundException {
		if (!specialtyRepository.existsById(specialtyId))
			throw new SpecialtyNotFoundException("Specialty not found...!");
		List<VetSpecialty> rel = (minExperience == null)
				? vetSpecialtyRepository.findBySpecialtyId(specialtyId)
				: vetSpecialtyRepository.findBySpecialtyIdAndYearsExperienceGreaterThanEqual(specialtyId, minExperience);
		List<Integer> ids = rel.stream().map(VetSpecialty::getVetId).toList();
		return vetMapper.mapToDtoList(vetRepository.findAllById(ids));
	}

	/**
	 * Marca la especialidad como principal y desmarca las demas del mismo veterinario
	 */
	@Override
	@Transactional
	public VetSpecialtyDTO setPrimary(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		VetSpecialty target = getEntity(vetId, specialtyId);
		for (VetSpecialty vs : vetSpecialtyRepository.findByVetId(vetId)) {
			vs.setPrimarySpecialty(vs.getSpecialtyId().equals(specialtyId));
			vetSpecialtyRepository.save(vs);
		}
		target.setPrimarySpecialty(true);
		return vetSpecialtyMapper.mapToDto(target);
	}

	@Override
	public VetSpecialtyDTO updateYearsExperience(Integer vetId, Integer specialtyId, Integer years)
			throws VetSpecialtyNotFoundException {
		VetSpecialty vs = getEntity(vetId, specialtyId);
		vs.setYearsExperience(years);
		return vetSpecialtyMapper.mapToDto(vetSpecialtyRepository.save(vs));
	}

	@Override
	public VetSpecialtyDTO updateCertificationDate(Integer vetId, Integer specialtyId, String certificationDate)
			throws VetSpecialtyNotFoundException {
		VetSpecialty vs = getEntity(vetId, specialtyId);
		vs.setCertificationDate(LocalDate.parse(certificationDate));
		return vetSpecialtyMapper.mapToDto(vetSpecialtyRepository.save(vs));
	}

	private VetSpecialty getEntity(Integer vetId, Integer specialtyId) throws VetSpecialtyNotFoundException {
		return vetSpecialtyRepository.findById(new VetSpecialtyId(vetId, specialtyId))
				.orElseThrow(() -> new VetSpecialtyNotFoundException("Specialty not assigned to vet...!"));
	}
}
