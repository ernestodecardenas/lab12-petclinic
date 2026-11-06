package com.tecsup.petclinic.webs;

import com.tecsup.petclinic.dtos.VetDTO;

import com.tecsup.petclinic.dtos.OwnerDTO;

import com.tecsup.petclinic.dtos.SpecialtyDTO;

import java.util.List;

/**
 * Helper que construye los objetos esperados para las pruebas con Mockito
 */
public class TObjectCreator {

	// ===================== VET =====================

	public static VetDTO getVetTO() {
		return VetDTO.builder().id(1).firstName("James").lastName("Carter")
				.email("james.carter@petclinic.com").phone("6085551234").active(true).build();
	}

	public static VetDTO newVetTO() {
		return VetDTO.builder().firstName("Gregory").lastName("House")
				.email("gregory.house@petclinic.com").phone("6085550000").active(true).build();
	}

	public static VetDTO newVetCreatedTO() {
		VetDTO vet = newVetTO();
		vet.setId(1000);
		return vet;
	}

	public static List<VetDTO> getAllVetTOs() {
		return List.of(
				VetDTO.builder().id(1).firstName("James").lastName("Carter").active(true).build(),
				VetDTO.builder().id(2).firstName("Helen").lastName("Leary").active(true).build(),
				VetDTO.builder().id(3).firstName("Linda").lastName("Douglas").active(true).build(),
				VetDTO.builder().id(4).firstName("Rafael").lastName("Ortega").active(true).build(),
				VetDTO.builder().id(5).firstName("Henry").lastName("Stevens").active(true).build(),
				VetDTO.builder().id(6).firstName("Sharon").lastName("Jenkins").active(false).build());
	}

	public static List<VetDTO> getActiveVetTOs() {
		return getAllVetTOs().stream().filter(VetDTO::getActive).toList();
	}

	public static List<VetDTO> getInactiveVetTOs() {
		return getAllVetTOs().stream().filter(v -> !v.getActive()).toList();
	}

	// ===================== OWNER =====================

	public static OwnerDTO getOwnerTO() {
		return OwnerDTO.builder().id(1).firstName("George").lastName("Franklin")
				.address("110 W. Liberty St.").city("Madison").telephone("6085551023").build();
	}

	public static OwnerDTO newOwnerTO() {
		return OwnerDTO.builder().firstName("Ana").lastName("Torres")
				.address("Av. Cascanueces 2221").city("Lima").telephone("987654321").build();
	}

	public static OwnerDTO newOwnerCreatedTO() {
		OwnerDTO owner = newOwnerTO();
		owner.setId(1000);
		return owner;
	}

	public static List<OwnerDTO> getAllOwnerTOs() {
		return List.of(
				OwnerDTO.builder().id(1).firstName("George").lastName("Franklin").build(),
				OwnerDTO.builder().id(2).firstName("Betty").lastName("Davis").build(),
				OwnerDTO.builder().id(3).firstName("Eduardo").lastName("Rodriquez").build(),
				OwnerDTO.builder().id(4).firstName("Harold").lastName("Davis").build());
	}

	public static List<OwnerDTO> getOwnersDavisTOs() {
		return getAllOwnerTOs().stream().filter(o -> o.getLastName().equals("Davis")).toList();
	}

	// ===================== SPECIALTY =====================

	public static SpecialtyDTO getSpecialtyTO() {
		return SpecialtyDTO.builder().id(1).name("radiology").office("Farewell").openHour(8).closeHour(18).build();
	}

	public static SpecialtyDTO newSpecialtyTO() {
		return SpecialtyDTO.builder().name("cardiology").office("Lima Norte").openHour(9).closeHour(17).build();
	}

	public static SpecialtyDTO newSpecialtyCreatedTO() {
		SpecialtyDTO specialty = newSpecialtyTO();
		specialty.setId(1000);
		return specialty;
	}

	public static List<SpecialtyDTO> getAllSpecialtyTOs() {
		return List.of(
				SpecialtyDTO.builder().id(1).name("radiology").office("Farewell").openHour(8).closeHour(18).build(),
				SpecialtyDTO.builder().id(2).name("surgery").office("Maryland").openHour(8).closeHour(12).build(),
				SpecialtyDTO.builder().id(3).name("dentistry").office("Terranova").openHour(9).closeHour(19).build());
	}
}
