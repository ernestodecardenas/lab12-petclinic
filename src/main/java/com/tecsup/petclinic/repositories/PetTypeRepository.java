package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.dtos.PetCountByTypeDTO;
import com.tecsup.petclinic.entities.PetType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PetTypeRepository extends JpaRepository<PetType, Integer> {

	List<PetType> findByActive(Boolean active);

	/**
	 * Reporte de cantidad de mascotas por tipo (solo tipos activos)
	 */
	@Query("select new com.tecsup.petclinic.dtos.PetCountByTypeDTO(t.id, t.name, count(p.id)) " +
			"from types t left join pets p on p.typeId = t.id " +
			"where t.active = true " +
			"group by t.id, t.name order by t.id")
	List<PetCountByTypeDTO> countPetsByActiveType();
}
