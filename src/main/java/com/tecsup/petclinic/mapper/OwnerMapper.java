package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.entities.Owner;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OwnerMapper {

	Owner mapToEntity(OwnerDTO ownerDTO);

	OwnerDTO mapToDto(Owner owner);

	List<OwnerDTO> mapToDtoList(List<Owner> owners);
}
