package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.entities.Specialty;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpecialtyMapper {

	Specialty mapToEntity(SpecialtyDTO specialtyDTO);

	SpecialtyDTO mapToDto(Specialty specialty);

	List<SpecialtyDTO> mapToDtoList(List<Specialty> specialties);
}
