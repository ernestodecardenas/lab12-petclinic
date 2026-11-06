package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.SpecialtyDTO;
import com.tecsup.petclinic.exceptions.SpecialtyNotFoundException;
import com.tecsup.petclinic.services.SpecialtyService;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de SpecialtyController con SpecialtyService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class SpecialtyControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SpecialtyService specialtyService;

	private ResultActions postSpecialty(SpecialtyDTO specialty) throws Exception {
		return mockMvc.perform(post("/specialties")
				.content(om.writeValueAsString(specialty))
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON));
	}

	@Test
	public void testFindAllSpecialties() throws Exception {
		Mockito.when(specialtyService.findAll()).thenReturn(TObjectCreator.getAllSpecialtyTOs());

		mockMvc.perform(get("/specialties"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(3)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindSpecialtyOK() throws Exception {
		SpecialtyDTO specialtyTO = TObjectCreator.getSpecialtyTO();
		Mockito.when(specialtyService.findById(1)).thenReturn(specialtyTO);

		mockMvc.perform(get("/specialties/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(specialtyTO.getId())))
				.andExpect(jsonPath("$.name", is(specialtyTO.getName())));
	}

	@Test
	public void testFindSpecialtyKO() throws Exception {
		Mockito.when(specialtyService.findById(666))
				.thenThrow(new SpecialtyNotFoundException("Record not found...!"));

		mockMvc.perform(get("/specialties/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateSpecialty() throws Exception {
		SpecialtyDTO created = TObjectCreator.newSpecialtyCreatedTO();
		Mockito.when(specialtyService.create(any(SpecialtyDTO.class))).thenReturn(created);

		postSpecialty(TObjectCreator.newSpecialtyTO())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(created.getId())))
				.andExpect(jsonPath("$.name", is(created.getName())));
	}

	@Test
	public void testFindByName() throws Exception {
		SpecialtyDTO surgery = TObjectCreator.getAllSpecialtyTOs().get(1);
		Mockito.when(specialtyService.findByName("surgery")).thenReturn(List.of(surgery));

		mockMvc.perform(get("/specialties").param("name", "surgery"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].name", is("surgery")));
	}

	@Test
	public void testFindByOffice() throws Exception {
		Mockito.when(specialtyService.findByOffice("Farewell"))
				.thenReturn(List.of(TObjectCreator.getSpecialtyTO()));

		mockMvc.perform(get("/specialties").param("office", "Farewell"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is("radiology")));
	}

	@Test
	public void testCreateSpecialty_InvalidSchedule() throws Exception {
		SpecialtyDTO invalid = TObjectCreator.newSpecialtyTO();
		invalid.setOpenHour(18);
		invalid.setCloseHour(9);

		postSpecialty(invalid).andExpect(status().isBadRequest());
		Mockito.verify(specialtyService, Mockito.never()).create(any(SpecialtyDTO.class));
	}

	@Test
	public void testCreateSpecialty_InvalidHourRange() throws Exception {
		SpecialtyDTO invalid = TObjectCreator.newSpecialtyTO();
		invalid.setOpenHour(-1);
		invalid.setCloseHour(25);

		postSpecialty(invalid).andExpect(status().isBadRequest());
		Mockito.verify(specialtyService, Mockito.never()).create(any(SpecialtyDTO.class));
	}

	@Test
	public void testDeleteSpecialty() throws Exception {
		Mockito.doNothing().when(specialtyService).delete(1000);

		mockMvc.perform(delete("/specialties/1000"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteSpecialtyKO() throws Exception {
		Mockito.doThrow(new SpecialtyNotFoundException("Record not found...!"))
				.when(specialtyService).delete(1000);

		mockMvc.perform(delete("/specialties/1000"))
				.andExpect(status().isNotFound());
	}
}
