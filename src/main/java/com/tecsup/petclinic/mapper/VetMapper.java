package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.entities.Vet;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface VetMapper {

	Vet mapToEntity(VetDTO vetDTO);

	VetDTO mapToDto(Vet vet);

	List<VetDTO> mapToDtoList(List<Vet> vets);
}
