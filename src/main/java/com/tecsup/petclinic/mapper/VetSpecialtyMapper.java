package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.entities.VetSpecialty;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VetSpecialtyMapper {

	@Mapping(source = "primary", target = "primarySpecialty")
	VetSpecialty mapToEntity(VetSpecialtyDTO dto);

	@Mapping(source = "primarySpecialty", target = "primary")
	VetSpecialtyDTO mapToDto(VetSpecialty entity);

	List<VetSpecialtyDTO> mapToDtoList(List<VetSpecialty> entities);
}
