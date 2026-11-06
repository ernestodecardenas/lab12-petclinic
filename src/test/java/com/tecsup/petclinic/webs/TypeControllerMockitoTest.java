package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.PetTypeDTO;
import com.tecsup.petclinic.exceptions.PetTypeNotFoundException;
import com.tecsup.petclinic.services.PetTypeService;
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

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de TypeController con PetTypeService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class TypeControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PetTypeService petTypeService;

	@Test
	public void testFindAllTypes() throws Exception {
		Mockito.when(petTypeService.findAll()).thenReturn(TObjectCreator.getAllPetTypeTOs());

		mockMvc.perform(get("/types"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(8)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindTypeOK() throws Exception {
		PetTypeDTO typeTO = TObjectCreator.getPetTypeTO();
		Mockito.when(petTypeService.findById(1)).thenReturn(typeTO);

		mockMvc.perform(get("/types/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.name", is("cat")));
	}

	@Test
	public void testFindTypeKO() throws Exception {
		Mockito.when(petTypeService.findById(666)).thenThrow(new PetTypeNotFoundException("Record not found...!"));

		mockMvc.perform(get("/types/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateType() throws Exception {
		PetTypeDTO created = TObjectCreator.newPetTypeCreatedTO();
		Mockito.when(petTypeService.create(any(PetTypeDTO.class))).thenReturn(created);

		mockMvc.perform(post("/types")
						.content(om.writeValueAsString(TObjectCreator.newPetTypeTO()))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1000)))
				.andExpect(jsonPath("$.name", is("ferret")));
	}

	@Test
	public void testCreateType_BadRequest() throws Exception {
		PetTypeDTO invalid = TObjectCreator.newPetTypeTO();
		invalid.setName("");

		mockMvc.perform(post("/types")
						.content(om.writeValueAsString(invalid))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
		Mockito.verify(petTypeService, Mockito.never()).create(any(PetTypeDTO.class));
	}

	@Test
	public void testGetPetCountByType() throws Exception {
		Mockito.when(petTypeService.getPetCountByType()).thenReturn(TObjectCreator.getPetCountByTypeTOs());

		mockMvc.perform(get("/types/report/pet-count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].typeName", is("cat")))
				.andExpect(jsonPath("$[0].petCount", is(4)));
	}

	@Test
	public void testDeleteType() throws Exception {
		Mockito.doNothing().when(petTypeService).delete(1000);

		mockMvc.perform(delete("/types/1000"))
				.andExpect(status().isOk());
	}

	@Test
	public void testFindActiveTypes() throws Exception {
		Mockito.when(petTypeService.findByActive(true)).thenReturn(
				TObjectCreator.getAllPetTypeTOs().stream().filter(PetTypeDTO::getActive).toList());

		mockMvc.perform(get("/types").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(7)))
				.andExpect(jsonPath("$[*].name", not(hasItem("snake"))));
	}

	@Test
	public void testGetPetCountByType_ExcludeInactive() throws Exception {
		Mockito.when(petTypeService.getPetCountByType()).thenReturn(TObjectCreator.getPetCountByTypeTOs());

		mockMvc.perform(get("/types/report/pet-count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(7)))
				.andExpect(jsonPath("$[*].typeName", not(hasItem("snake"))));
	}

	@Test
	public void testDeleteTypeKO() throws Exception {
		Mockito.doThrow(new PetTypeNotFoundException("Record not found...!")).when(petTypeService).delete(1000);

		mockMvc.perform(delete("/types/1000"))
				.andExpect(status().isNotFound());
	}
}
