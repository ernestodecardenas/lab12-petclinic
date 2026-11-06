package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.tecsup.petclinic.dtos.PetTypeDTO;
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

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de integracion reales (H2) para TypeController.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Transactional
@Slf4j
public class TypeControllerTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	private Integer createType(PetTypeDTO type) throws Exception {
		ResultActions mvcActions = mockMvc.perform(post("/types")
						.content(om.writeValueAsString(type))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated());
		return JsonPath.parse(mvcActions.andReturn().getResponse().getContentAsString()).read("$.id");
	}

	/** Cantidad real de mascotas de un tipo, leida desde GET /pets */
	private int countPetsOfType(int typeId) throws Exception {
		String pets = mockMvc.perform(get("/pets")).andReturn().getResponse().getContentAsString();
		List<Integer> ids = JsonPath.parse(pets).read("$[?(@.typeId == " + typeId + ")].id");
		return ids.size();
	}

	// ---------------- Integrante A : Listado y consulta ----------------

	@Test
	public void testFindAllTypes() throws Exception {
		mockMvc.perform(get("/types"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(8)))
				.andExpect(jsonPath("$[0].id", is(1)))
				.andExpect(jsonPath("$[0].name", is("cat")));
	}

	@Test
	public void testFindTypeOK() throws Exception {
		mockMvc.perform(get("/types/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("cat")))
				.andExpect(jsonPath("$.description", is("Domestic feline")))
				.andExpect(jsonPath("$.active", is(true)))
				.andExpect(jsonPath("$.averageLifespan", is(15)));
	}

	@Test
	public void testFindTypeKO() throws Exception {
		mockMvc.perform(get("/types/666"))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante B : Creacion y reporte ----------------

	@Test
	public void testCreateType() throws Exception {
		PetTypeDTO newType = TObjectCreator.newPetTypeTO();

		mockMvc.perform(post("/types")
						.content(om.writeValueAsString(newType))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", notNullValue()))
				.andExpect(jsonPath("$.name", is(newType.getName())))
				.andExpect(jsonPath("$.sizeCategory", is(newType.getSizeCategory())))
				.andExpect(jsonPath("$.careLevel", is(newType.getCareLevel())))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testGetPetCountByType() throws Exception {
		// Con los datos semilla cat = 4 (Leo, Samantha, Max, Sly). Se compara contra
		// las mascotas reales de /pets por si otra clase de prueba agrego mascotas.
		int cats = countPetsOfType(1);

		mockMvc.perform(get("/types/report/pet-count"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].typeId", is(1)))
				.andExpect(jsonPath("$[0].typeName", is("cat")))
				.andExpect(jsonPath("$[0].petCount", is(cats)))
				.andExpect(jsonPath("$[0].petCount", greaterThanOrEqualTo(4)));
	}

	@Test
	public void testDeleteType() throws Exception {
		Integer id = createType(TObjectCreator.newPetTypeTO());

		mockMvc.perform(delete("/types/" + id))
				.andExpect(status().isOk());

		mockMvc.perform(get("/types/" + id))
				.andExpect(status().isNotFound());
	}

	// ---------------- Integrante C : Filtros y errores ----------------

	@Test
	public void testFindActiveTypes() throws Exception {
		mockMvc.perform(get("/types").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(7)))
				.andExpect(jsonPath("$[*].name", not(hasItem("snake"))))
				.andExpect(jsonPath("$[*].active", everyItem(is(true))));
	}

	@Test
	public void testGetPetCountByType_ExcludeInactive() throws Exception {
		// snake (id 6) esta inactivo -> no debe aparecer aunque tenga a Basil
		mockMvc.perform(get("/types/report/pet-count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(7)))
				.andExpect(jsonPath("$[*].typeName", not(hasItem("snake"))))
				.andExpect(jsonPath("$[*].typeId", not(hasItem(6))));
	}

	@Test
	public void testDeleteTypeKO() throws Exception {
		mockMvc.perform(delete("/types/1000"))
				.andExpect(status().isNotFound());
	}
}
