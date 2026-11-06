package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VisitDTO;
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
 * Pruebas de integracion reales (H2) para VisitController.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class VisitControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	private Integer createVisit(VisitDTO visit) throws Exception {
		ResultActions mvcActions = mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(visit))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		return JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");
	}

	// ---------------- Integrante A : Listado y consulta ----------------

	@Test
	public void testFindAllVisits() throws Exception {
		mockMvc.perform(get("/visits"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVisitOK() throws Exception {
		mockMvc.perform(get("/visits/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.petId", is(7)))
				.andExpect(jsonPath("$.vetId", is(2)))
				.andExpect(jsonPath("$.visitDate", is("2010-03-04")))
				.andExpect(jsonPath("$.description", is("rabies shot")))
				.andExpect(jsonPath("$.cost", is(45.0)));
	}

	@Test
	public void testFindVisitKO() throws Exception {
		mockMvc.perform(get("/visits/666"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante B : Creacion y borrado ----------------

	@Test
	public void testCreateVisit() throws Exception {
		VisitDTO newVisit = TObjectCreator.newVisitTO();

		mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(newVisit))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.petId", is(newVisit.getPetId())))
				.andExpect(jsonPath("$.vetId", is(newVisit.getVetId())))
				.andExpect(jsonPath("$.visitDate", is(newVisit.getVisitDate())))
				.andExpect(jsonPath("$.description", is(newVisit.getDescription())))
				.andExpect(jsonPath("$.cost", is(80.5)));
	}

	@Test
	public void testCreateVisit_BadRequest() throws Exception {
		VisitDTO invalid = TObjectCreator.newVisitTO();
		invalid.setPetId(null);

		mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(invalid))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testUpdateVisit() throws Exception {
		Integer id = createVisit(TObjectCreator.newVisitTO());

		VisitDTO upVisit = TObjectCreator.newVisitTO();
		upVisit.setDescription("vaccination - booster");
		upVisit.setCost(new java.math.BigDecimal("99.90"));

		mockMvc.perform(put("/visits/" + id)
						.content(om.writeValueAsString(upVisit))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		mockMvc.perform(get("/visits/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description", is("vaccination - booster")))
				.andExpect(jsonPath("$.cost", is(99.9)));
	}

	@Test
	public void testDeleteVisit() throws Exception {
		Integer id = createVisit(TObjectCreator.newVisitTO());

		mockMvc.perform(delete("/visits/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/visits/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVisitKO() throws Exception {
		mockMvc.perform(delete("/visits/1000"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante C : Consultas por relacion ----------------

	@Test
	public void testFindByPetId() throws Exception {
		// Samantha (pet 7) tiene 2 visitas: ids 1 y 4
		mockMvc.perform(get("/visits").param("petId", "7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 4)))
				.andExpect(jsonPath("$[*].petId", everyItem(is(7))));
	}

	@Test
	public void testFindByVetId() throws Exception {
		// Vet 2 (Helen Leary) atendio las visitas 1 y 2
		mockMvc.perform(get("/visits").param("vetId", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(1, 2)))
				.andExpect(jsonPath("$[*].vetId", everyItem(is(2))));
	}

	@Test
	public void testCalculateTotalCostByPet() throws Exception {
		// pet 7 : 45.00 + 275.00 = 320.00
		mockMvc.perform(get("/visits/pet/7/total-cost"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.petId", is(7)))
				.andExpect(jsonPath("$.totalCost", is(320.0)));
	}

	// ---------------- Integrante D : Consultas por fecha ----------------

	@Test
	public void testFindByDateBetween() throws Exception {
		mockMvc.perform(get("/visits").param("from", "2024-01-01").param("to", "2024-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(5, 6)));
	}

	@Test
	public void testFindByVetIdAndDateBetween() throws Exception {
		mockMvc.perform(get("/visits").param("vetId", "1")
						.param("from", "2024-01-01").param("to", "2024-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(5)))
				.andExpect(jsonPath("$[0].description", is("general checkup")));
	}

	@Test
	public void testCountVisitsByPet() throws Exception {
		mockMvc.perform(get("/visits/pet/8/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.petId", is(8)))
				.andExpect(jsonPath("$.count", is(2)));
	}
}
