package com.tecsup.petclinic.services;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.mapper.VisitMapper;
import com.tecsup.petclinic.repositories.VisitRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
@Slf4j
public class VisitServiceImpl implements VisitService {

	private final VisitRepository visitRepository;
	private final VisitMapper visitMapper;

	public VisitServiceImpl(VisitRepository visitRepository, VisitMapper visitMapper) {
		this.visitRepository = visitRepository;
		this.visitMapper = visitMapper;
	}

	@Override
	public VisitDTO create(VisitDTO visitDTO) {
		Visit visit = visitMapper.mapToEntity(visitDTO);
		visit.setId(null);
		return visitMapper.mapToDto(visitRepository.save(visit));
	}

	@Override
	public VisitDTO update(Integer id, VisitDTO visitDTO) throws VisitNotFoundException {
		Visit visit = getEntity(id);
		Visit changes = visitMapper.mapToEntity(visitDTO);
		visit.setPetId(changes.getPetId());
		visit.setVetId(changes.getVetId());
		visit.setVisitDate(changes.getVisitDate());
		visit.setDescription(changes.getDescription());
		visit.setCost(changes.getCost());
		return visitMapper.mapToDto(visitRepository.save(visit));
	}

	@Override
	public void delete(Integer id) throws VisitNotFoundException {
		visitRepository.delete(getEntity(id));
	}

	@Override
	public VisitDTO findById(Integer id) throws VisitNotFoundException {
		return visitMapper.mapToDto(getEntity(id));
	}

	@Override
	public List<VisitDTO> findAll() {
		return visitMapper.mapToDtoList(visitRepository.findAll());
	}

	@Override
	public List<VisitDTO> findByPetId(Integer petId) {
		return visitMapper.mapToDtoList(visitRepository.findByPetId(petId));
	}

	@Override
	public List<VisitDTO> findByVetId(Integer vetId) {
		return visitMapper.mapToDtoList(visitRepository.findByVetId(vetId));
	}

	@Override
	public List<VisitDTO> findByDateBetween(LocalDate from, LocalDate to) {
		return visitMapper.mapToDtoList(visitRepository.findByVisitDateBetween(from, to));
	}

	@Override
	public List<VisitDTO> findByVetIdAndDateBetween(Integer vetId, LocalDate from, LocalDate to) {
		return visitMapper.mapToDtoList(visitRepository.findByVetIdAndVisitDateBetween(vetId, from, to));
	}

	@Override
	public BigDecimal calculateTotalCostByPet(Integer petId) {
		return visitRepository.sumCostByPetId(petId);
	}

	@Override
	public long countVisitsByPet(Integer petId) {
		return visitRepository.countByPetId(petId);
	}

	private Visit getEntity(Integer id) throws VisitNotFoundException {
		return visitRepository.findById(id)
				.orElseThrow(() -> new VisitNotFoundException("Record not found...!"));
	}
}
