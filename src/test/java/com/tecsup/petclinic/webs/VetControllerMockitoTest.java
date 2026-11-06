package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VetDTO;
import com.tecsup.petclinic.exceptions.VetNotFoundException;
import com.tecsup.petclinic.services.VetService;
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
 * Pruebas del controlador VetController con el servicio mockeado (@MockitoBean).
 * Solo se valida la logica del controlador y el contrato JSON, sin tocar la BD.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VetControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VetService vetService;

	@Test
	public void testFindAllVets() throws Exception {
		Mockito.when(vetService.findAll()).thenReturn(TObjectCreator.getAllVetTOs());

		mockMvc.perform(get("/vets"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVetOK() throws Exception {
		VetDTO vetTO = TObjectCreator.getVetTO();
		Mockito.when(vetService.findById(vetTO.getId())).thenReturn(vetTO);

		mockMvc.perform(get("/vets/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(vetTO.getId())))
				.andExpect(jsonPath("$.firstName", is(vetTO.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(vetTO.getLastName())));
	}

	@Test
	public void testFindVetKO() throws Exception {
		Mockito.when(vetService.findById(666)).thenThrow(new VetNotFoundException("Record not found...!"));

		mockMvc.perform(get("/vets/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateVet() throws Exception {
		VetDTO newVet = TObjectCreator.newVetTO();
		VetDTO created = TObjectCreator.newVetCreatedTO();
		Mockito.when(vetService.create(any(VetDTO.class))).thenReturn(created);

		mockMvc.perform(post("/vets")
						.content(om.writeValueAsString(newVet))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(created.getId())))
				.andExpect(jsonPath("$.lastName", is(created.getLastName())));
	}

	@Test
	public void testDeactivateVet() throws Exception {
		VetDTO deactivated = TObjectCreator.newVetCreatedTO();
		deactivated.setActive(false);
		Mockito.when(vetService.changeActive(1000, false)).thenReturn(deactivated);

		mockMvc.perform(put("/vets/1000/deactivate"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.active", is(false)));
	}

	@Test
	public void testReactivateVet() throws Exception {
		VetDTO reactivated = VetDTO.builder().id(6).firstName("Sharon").lastName("Jenkins").active(true).build();
		Mockito.when(vetService.changeActive(6, true)).thenReturn(reactivated);

		mockMvc.perform(put("/vets/6/activate"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(6)))
				.andExpect(jsonPath("$.active", is(true)));
	}

	@Test
	public void testFindActiveVets() throws Exception {
		Mockito.when(vetService.findByActive(true)).thenReturn(TObjectCreator.getActiveVetTOs());

		mockMvc.perform(get("/vets").param("active", "true"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(5)));
	}

	@Test
	public void testFindInactiveVets() throws Exception {
		Mockito.when(vetService.findByActive(false)).thenReturn(TObjectCreator.getInactiveVetTOs());

		mockMvc.perform(get("/vets").param("active", "false"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(6)));
	}

	@Test
	public void testDeleteVet() throws Exception {
		Mockito.doNothing().when(vetService).delete(1000);

		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteVetKO() throws Exception {
		Mockito.doThrow(new VetNotFoundException("Record not found...!")).when(vetService).delete(1000);

		mockMvc.perform(delete("/vets/1000"))
				.andExpect(status().isNotFound());
	}
}
