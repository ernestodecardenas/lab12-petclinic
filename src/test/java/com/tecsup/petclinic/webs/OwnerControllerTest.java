package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.OwnerDTO;
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
 * Pruebas de integracion reales (H2) para OwnerController.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class OwnerControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	private Integer createOwner(OwnerDTO owner) throws Exception {
		ResultActions mvcActions = mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(owner))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		return JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");
	}

	// ---------------- Integrante A : Listado y consulta ----------------

	@Test
	public void testFindAllOwners() throws Exception {
		mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindOwnerOK() throws Exception {
		mockMvc.perform(get("/owners/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.firstName", is("George")))
				.andExpect(jsonPath("$.lastName", is("Franklin")))
				.andExpect(jsonPath("$.city", is("Madison")));
	}

	@Test
	public void testFindOwnerKO() throws Exception {
		mockMvc.perform(get("/owners/666"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante B : Creacion ----------------

	@Test
	public void testCreateOwner() throws Exception {
		OwnerDTO newOwner = TObjectCreator.newOwnerTO();

		mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(newOwner))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.firstName", is(newOwner.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(newOwner.getLastName())))
				.andExpect(jsonPath("$.address", is(newOwner.getAddress())))
				.andExpect(jsonPath("$.city", is(newOwner.getCity())))
				.andExpect(jsonPath("$.telephone", is(newOwner.getTelephone())));
	}

	@Test
	public void testCreateOwner_BadRequest() throws Exception {
		OwnerDTO invalid = TObjectCreator.newOwnerTO();
		invalid.setLastName(null);

		mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(invalid))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void testFindByLastName() throws Exception {
		mockMvc.perform(get("/owners").param("lastName", "Davis"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].lastName", everyItem(is("Davis"))))
				.andExpect(jsonPath("$[*].firstName", containsInAnyOrder("Betty", "Harold")));
	}

	// ---------------- Integrante C : Actualizacion y borrado ----------------

	@Test
	public void testUpdateOwner() throws Exception {
		Integer id = createOwner(TObjectCreator.newOwnerTO());

		OwnerDTO upOwner = TObjectCreator.newOwnerTO();
		upOwner.setLastName("Torres Updated");
		upOwner.setCity("Arequipa");

		mockMvc.perform(put("/owners/" + id)
						.content(om.writeValueAsString(upOwner))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk());

		mockMvc.perform(get("/owners/" + id))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(id)))
				.andExpect(jsonPath("$.lastName", is("Torres Updated")))
				.andExpect(jsonPath("$.city", is("Arequipa")));
	}

	@Test
	public void testDeleteOwner() throws Exception {
		Integer id = createOwner(TObjectCreator.newOwnerTO());

		mockMvc.perform(delete("/owners/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/owners/" + id))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testDeleteOwnerKO() throws Exception {
		mockMvc.perform(delete("/owners/1000"))
				.andExpect(status().isNotFound());
	}
}
