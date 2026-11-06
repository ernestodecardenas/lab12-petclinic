package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
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
 * Pruebas de integracion reales (H2) para SpecialtyController.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class SpecialtyControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	private ResultActions postSpecialty(SpecialtyDTO specialty) throws Exception {
		return mockMvc.perform(post("/specialties")
				.content(om.writeValueAsString(specialty))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
	}

	// ---------------- Integrante A : Listado y consulta ----------------

	@Test
	public void testFindAllSpecialties() throws Exception {
		mockMvc.perform(get("/specialties"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[*].name", contains("radiology", "surgery", "dentistry")));
	}

	@Test
	public void testFindSpecialtyOK() throws Exception {
		mockMvc.perform(get("/specialties/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("radiology")))
				.andExpect(jsonPath("$.office", is("Farewell")))
				.andExpect(jsonPath("$.openHour", is(8)))
				.andExpect(jsonPath("$.closeHour", is(18)));
	}

	@Test
	public void testFindSpecialtyKO() throws Exception {
		mockMvc.perform(get("/specialties/666"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante B : Creacion y busqueda ----------------

	@Test
	public void testCreateSpecialty() throws Exception {
		SpecialtyDTO newSpecialty = TObjectCreator.newSpecialtyTO();

		postSpecialty(newSpecialty)
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.name", is(newSpecialty.getName())))
				.andExpect(jsonPath("$.office", is(newSpecialty.getOffice())))
				.andExpect(jsonPath("$.openHour", is(newSpecialty.getOpenHour())))
				.andExpect(jsonPath("$.closeHour", is(newSpecialty.getCloseHour())));
	}

	@Test
	public void testFindByName() throws Exception {
		mockMvc.perform(get("/specialties").param("name", "surgery"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(2)))
				.andExpect(jsonPath("$[0].name", is("surgery")))
				.andExpect(jsonPath("$[0].office", is("Maryland")));
	}

	@Test
	public void testFindByOffice() throws Exception {
		mockMvc.perform(get("/specialties").param("office", "Farewell"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is("radiology")));
	}

	// ---------------- Integrante C : Validaciones y borrado ----------------

	@Test
	public void testCreateSpecialty_InvalidSchedule() throws Exception {
		SpecialtyDTO invalid = TObjectCreator.newSpecialtyTO();
		invalid.setOpenHour(18);
		invalid.setCloseHour(9);   // h_open >= h_close

		postSpecialty(invalid)
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testCreateSpecialty_InvalidHourRange() throws Exception {
		SpecialtyDTO invalid = TObjectCreator.newSpecialtyTO();
		invalid.setOpenHour(-1);
		invalid.setCloseHour(25);  // fuera de 0-23

		postSpecialty(invalid)
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testDeleteSpecialty() throws Exception {
		ResultActions mvcActions = postSpecialty(TObjectCreator.newSpecialtyTO())
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/specialties/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/specialties/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteSpecialtyKO() throws Exception {
		mockMvc.perform(delete("/specialties/1000"))
				.andExpect(status().isNotFound());
	}
}
