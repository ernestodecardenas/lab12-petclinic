package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.VetDTO;
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
 * Pruebas de integracion reales (controlador -> servicio -> repositorio -> H2)
 * para VetController. Cada prueba corre en una transaccion que se revierte
 * al terminar, por lo que los datos semilla no se alteran entre pruebas.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class VetControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	// ---------------- Integrante A : Listado y consulta ----------------

	@Test
	public void testFindAllVets() throws Exception {
		this.mockMvc.perform(get("/vets"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].lastName", is("Carter")));
	}

	@Test
	public void testFindVetOK() throws Exception {
		this.mockMvc.perform(get("/vets/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("James")))
				.andExpect(jsonPath("$.lastName", is("Carter")))
				.andExpect(jsonPath("$.email", is("james.carter@petclinic.com")))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testFindVetKO() throws Exception {
		mockMvc.perform(get("/vets/666"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante B : Creacion y soft delete ----------------

	@Test
	public void testCreateVet() throws Exception {
		VetDTO newVet = TObjectCreator.newVetTO();

		mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVet))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.firstName", is(newVet.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(newVet.getLastName())))
				.andExpect(jsonPath("$.email", is(newVet.getEmail())))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testDeactivateVet() throws Exception {
		// CREATE
		ResultActions mvcActions = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(TObjectCreator.newVetTO()))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		// DEACTIVATE
		mockMvc.perform(put("/vets/" + id + "/deactivate"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.active", is(false)));

		// FIND : confirma el cambio en BD
		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));
	}

	@Test
	public void testReactivateVet() throws Exception {
		// Vet 6 (Sharon Jenkins) esta inactivo en los datos semilla
		mockMvc.perform(get("/vets/6"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));

		mockMvc.perform(put("/vets/6/activate"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(6)))
				.andExpect(jsonPath("$.active", is(true)));
	}

	// ---------------- Integrante C : Filtros y borrado ----------------

	@Test
	public void testFindActiveVets() throws Exception {
		mockMvc.perform(get("/vets").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)))
				.andExpect(jsonPath("$[*].active", everyItem(is(true))));
	}

	@Test
	public void testFindInactiveVets() throws Exception {
		mockMvc.perform(get("/vets").param("active", "false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(6)))
				.andExpect(jsonPath("$[0].lastName", is("Jenkins")));
	}

	@Test
	public void testDeleteVet() throws Exception {
		ResultActions mvcActions = mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(TObjectCreator.newVetTO()))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		Integer id = JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");

		mockMvc.perform(delete("/vets/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/vets/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteVetKO() throws Exception {
		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isNotFound());
	}
}
