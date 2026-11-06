package com.tecsup.petclinic.mapper;

import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.entities.Visit;
import org.mapstruct.Mapper;

import java.util.List;

/**
 * LocalDate <-> String (ISO yyyy-MM-dd) lo convierte MapStruct automaticamente
 */
@Mapper(componentModel = "spring")
public interface VisitMapper {

	Visit mapToEntity(VisitDTO visitDTO);

	VisitDTO mapToDto(Visit visit);

	List<VisitDTO> mapToDtoList(List<Visit> visits);
}
