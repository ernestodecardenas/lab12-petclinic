package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetSpecialtyDTO;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integracion reales (H2) para VetSpecialtyController.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class VetSpecialtyControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	private ResultActions assign(VetSpecialtyDTO dto) throws Exception {
		return mockMvc.perform(post("/vet-specialties")
				.content(om.writeValueAsString(dto))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
	}

	// ---------------- Integrante A : Asignacion ----------------

	@Test
	public void testAssignSpecialtyToVet() throws Exception {
		VetSpecialtyDTO dto = TObjectCreator.newVetSpecialtyTO();

		assign(dto)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.vetId", is(1)))
				.andExpect(jsonPath("$.specialtyId", is(3)))
				.andExpect(jsonPath("$.yearsExperience", is(4)))
				.andExpect(jsonPath("$.certificationDate", is("2021-07-01")))
				.andExpect(jsonPath("$.primary", is(false)));

		mockMvc.perform(get("/vets/1/specialties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is("dentistry")));
	}

	@Test
	public void testRemoveSpecialtyFromVet() throws Exception {
		mockMvc.perform(delete("/vet-specialties/3/3"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vets/3/specialties"))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is("surgery")));
	}

	@Test
	public void testRemoveSpecialtyFromVetKO() throws Exception {
		mockMvc.perform(delete("/vet-specialties/1/1"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testAssignDuplicate_ShouldFail() throws Exception {
		VetSpecialtyDTO dto = TObjectCreator.newVetSpecialtyTO();

		assign(dto).andExpect(status().isCreated());
		assign(dto).andExpect(status().isConflict());
	}

	@Test
	public void testAssignSpecialty_VetNotFound() throws Exception {
		VetSpecialtyDTO dto = TObjectCreator.newVetSpecialtyTO();
		dto.setVetId(666);

		assign(dto).andExpect(status().isNotFound());
	}

	// ---------------- Integrante B : Consultas ----------------

	@Test
	public void testFindSpecialtiesByVet() throws Exception {
		mockMvc.perform(get("/vets/3/specialties"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].name", containsInAnyOrder("surgery", "dentistry")));
	}

	@Test
	public void testFindVetsBySpecialty() throws Exception {
		mockMvc.perform(get("/specialties/1/vets"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(2, 5)))
				.andExpect(jsonPath("$[*].lastName", containsInAnyOrder("Leary", "Stevens")));
	}

	@Test
	public void testFindSpecialtiesByVet_Empty() throws Exception {
		mockMvc.perform(get("/vets/1/specialties"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(0)));
	}

	// ---------------- Integrante C : Especialidad principal ----------------

	@Test
	public void testSetPrimarySpecialty() throws Exception {
		mockMvc.perform(put("/vet-specialties/3/3/primary"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.vetId", is(3)))
				.andExpect(jsonPath("$.specialtyId", is(3)))
				.andExpect(jsonPath("$.primary", is(true)));
	}

	@Test
	public void testSetPrimarySpecialty_OnlyOnePrimary() throws Exception {
		// Vet 3: surgery (2) es principal; se marca dentistry (3)
		mockMvc.perform(put("/vet-specialties/3/3/primary"))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vet-specialties").param("vetId", "3"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[?(@.primary == true)].specialtyId", contains(3)))
				.andExpect(jsonPath("$[?(@.specialtyId == 2)].primary", contains(false)));
	}

	@Test
	public void testSetPrimarySpecialty_NotAssigned() throws Exception {
		mockMvc.perform(put("/vet-specialties/1/1/primary"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante D : Experiencia y certificaciones ----------------

	@Test
	public void testFindVetsByMinExperience() throws Exception {
		mockMvc.perform(get("/specialties/1/vets").param("minExperience", "7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(2, 5)));

		mockMvc.perform(get("/specialties/1/vets").param("minExperience", "8"))
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(2)));
	}

	@Test
	public void testUpdateYearsExperience() throws Exception {
		mockMvc.perform(put("/vet-specialties/5/1/experience")
						.content("{\"yearsExperience\": 10}")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.vetId", is(5)))
				.andExpect(jsonPath("$.yearsExperience", is(10)));
	}

	@Test
	public void testUpdateCertificationDate() throws Exception {
		mockMvc.perform(put("/vet-specialties/5/1/certification")
						.content("{\"certificationDate\": \"2023-08-15\"}")
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.certificationDate", is("2023-08-15")));
	}
}
