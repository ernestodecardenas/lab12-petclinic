package com.tecsup.petclinic.repositories;

import com.tecsup.petclinic.entities.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitRepository extends JpaRepository<Visit, Integer> {

	List<Visit> findByPetId(Integer petId);

	List<Visit> findByVetId(Integer vetId);

	List<Visit> findByVisitDateBetween(LocalDate from, LocalDate to);

	List<Visit> findByVetIdAndVisitDateBetween(Integer vetId, LocalDate from, LocalDate to);

	long countByPetId(Integer petId);

	@Query("select coalesce(sum(v.cost), 0) from visits v where v.petId = :petId")
	BigDecimal sumCostByPetId(@Param("petId") Integer petId);
}
