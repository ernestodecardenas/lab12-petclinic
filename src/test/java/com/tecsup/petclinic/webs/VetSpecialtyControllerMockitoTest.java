package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import com.tecsup.petclinic.exceptions.VetSpecialtyAlreadyExistsException;
import com.tecsup.petclinic.exceptions.VetSpecialtyNotFoundException;
import com.tecsup.petclinic.services.VetSpecialtyService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de VetSpecialtyController con VetSpecialtyService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetSpecialtyControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VetSpecialtyService vetSpecialtyService;

	@Test
	public void testAssignSpecialtyToVet() throws Exception {
		VetSpecialtyDTO dto = TObjectCreator.newVetSpecialtyTO();
		Mockito.when(vetSpecialtyService.assign(any(VetSpecialtyDTO.class))).thenReturn(dto);

		mockMvc.perform(post("/vet-specialties")
						.content(om.writeValueAsString(dto))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.vetId", is(1)))
				.andExpect(jsonPath("$.specialtyId", is(3)));
	}

	@Test
	public void testRemoveSpecialtyFromVet() throws Exception {
		Mockito.doNothing().when(vetSpecialtyService).remove(3, 3);

		mockMvc.perform(delete("/vet-specialties/3/3"))
				.andExpect(status().isOk());
	}

	@Test
	public void testAssignDuplicate_ShouldFail() throws Exception {
		Mockito.when(vetSpecialtyService.assign(any(VetSpecialtyDTO.class)))
				.thenThrow(new VetSpecialtyAlreadyExistsException("Specialty already assigned to vet...!"));

		mockMvc.perform(post("/vet-specialties")
						.content(om.writeValueAsString(TObjectCreator.newVetSpecialtyTO()))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isConflict());
	}

	@Test
	public void testFindSpecialtiesByVet() throws Exception {
		List<SpecialtyDTO> specialties = List.of(
				TObjectCreator.getAllSpecialtyTOs().get(1), TObjectCreator.getAllSpecialtyTOs().get(2));
		Mockito.when(vetSpecialtyService.findSpecialtiesByVet(3)).thenReturn(specialties);

		mockMvc.perform(get("/vets/3/specialties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].name", containsInAnyOrder("surgery", "dentistry")));
	}

	@Test
	public void testFindVetsBySpecialty() throws Exception {
		List<VetDTO> vets = List.of(TObjectCreator.getAllVetTOs().get(1), TObjectCreator.getAllVetTOs().get(4));
		Mockito.when(vetSpecialtyService.findVetsBySpecialty(1, null)).thenReturn(vets);

		mockMvc.perform(get("/specialties/1/vets"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(2, 5)));
	}

	@Test
	public void testFindSpecialtiesByVet_Empty() throws Exception {
		Mockito.when(vetSpecialtyService.findSpecialtiesByVet(1)).thenReturn(List.of());

		mockMvc.perform(get("/vets/1/specialties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	@Test
	public void testSetPrimarySpecialty() throws Exception {
		VetSpecialtyDTO primary = TObjectCreator.getVet3SpecialtyTOs().get(1);
		primary.setPrimary(true);
		Mockito.when(vetSpecialtyService.setPrimary(3, 3)).thenReturn(primary);

		mockMvc.perform(put("/vet-specialties/3/3/primary"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.primary", is(true)));
	}

	@Test
	public void testSetPrimarySpecialty_OnlyOnePrimary() throws Exception {
		List<VetSpecialtyDTO> after = TObjectCreator.getVet3SpecialtyTOs();
		after.get(0).setPrimary(false);
		after.get(1).setPrimary(true);
		Mockito.when(vetSpecialtyService.findByVetId(3)).thenReturn(after);

		mockMvc.perform(get("/vet-specialties").param("vetId", "3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[?(@.primary == true)].specialtyId", contains(3)));
	}

	@Test
	public void testSetPrimarySpecialty_NotAssigned() throws Exception {
		Mockito.when(vetSpecialtyService.setPrimary(1, 1))
				.thenThrow(new VetSpecialtyNotFoundException("Specialty not assigned to vet...!"));

		mockMvc.perform(put("/vet-specialties/1/1/primary"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testFindVetsByMinExperience() throws Exception {
		List<VetDTO> vets = List.of(TObjectCreator.getAllVetTOs().get(1), TObjectCreator.getAllVetTOs().get(4));
		Mockito.when(vetSpecialtyService.findVetsBySpecialty(1, 7)).thenReturn(vets);

		mockMvc.perform(get("/specialties/1/vets").param("minExperience", "7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testUpdateYearsExperience() throws Exception {
		VetSpecialtyDTO updated = VetSpecialtyDTO.builder().vetId(5).specialtyId(1).yearsExperience(10).build();
		Mockito.when(vetSpecialtyService.updateYearsExperience(5, 1, 10)).thenReturn(updated);

		mockMvc.perform(put("/vet-specialties/5/1/experience")
						.content("{\"yearsExperience\": 10}")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.yearsExperience", is(10)));
	}

	@Test
	public void testUpdateCertificationDate() throws Exception {
		VetSpecialtyDTO updated = VetSpecialtyDTO.builder().vetId(5).specialtyId(1).certificationDate("2023-08-15").build();
		Mockito.when(vetSpecialtyService.updateCertificationDate(5, 1, "2023-08-15")).thenReturn(updated);

		mockMvc.perform(put("/vet-specialties/5/1/certification")
						.content("{\"certificationDate\": \"2023-08-15\"}")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.certificationDate", is("2023-08-15")));
	}
}
