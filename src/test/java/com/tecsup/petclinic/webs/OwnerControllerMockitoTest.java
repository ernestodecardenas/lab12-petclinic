package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.OwnerDTO;
import com.tecsup.petclinic.exceptions.OwnerNotFoundException;
import com.tecsup.petclinic.services.OwnerService;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de OwnerController con OwnerService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class OwnerControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private OwnerService ownerService;

	@Test
	public void testFindAllOwners() throws Exception {
		Mockito.when(ownerService.findAll()).thenReturn(TObjectCreator.getAllOwnerTOs());

		mockMvc.perform(get("/owners"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
				.andExpect(jsonPath("$", hasSize(4)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindOwnerOK() throws Exception {
		OwnerDTO ownerTO = TObjectCreator.getOwnerTO();
		Mockito.when(ownerService.findById(ownerTO.getId())).thenReturn(ownerTO);

		mockMvc.perform(get("/owners/1"))
				.andExpect(content().contentType(MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(ownerTO.getId())))
				.andExpect(jsonPath("$.lastName", is(ownerTO.getLastName())));
	}

	@Test
	public void testFindOwnerKO() throws Exception {
		Mockito.when(ownerService.findById(666)).thenThrow(new OwnerNotFoundException("Record not found...!"));

		mockMvc.perform(get("/owners/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateOwner() throws Exception {
		OwnerDTO created = TObjectCreator.newOwnerCreatedTO();
		Mockito.when(ownerService.create(any(OwnerDTO.class))).thenReturn(created);

		mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(TObjectCreator.newOwnerTO()))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andDo(print())
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(created.getId())))
				.andExpect(jsonPath("$.firstName", is(created.getFirstName())))
				.andExpect(jsonPath("$.lastName", is(created.getLastName())));
	}

	@Test
	public void testCreateOwner_BadRequest() throws Exception {
		OwnerDTO invalid = TObjectCreator.newOwnerTO();
		invalid.setLastName(null);

		mockMvc.perform(post("/owners")
						.content(om.writeValueAsString(invalid))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());

		// El servicio nunca debe invocarse con un cuerpo invalido
		Mockito.verify(ownerService, Mockito.never()).create(any(OwnerDTO.class));
	}

	@Test
	public void testFindByLastName() throws Exception {
		Mockito.when(ownerService.findByLastName("Davis")).thenReturn(TObjectCreator.getOwnersDavisTOs());

		mockMvc.perform(get("/owners").param("lastName", "Davis"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testUpdateOwner() throws Exception {
		OwnerDTO updated = TObjectCreator.newOwnerCreatedTO();
		updated.setLastName("Torres Updated");
		Mockito.when(ownerService.update(eq(1000), any(OwnerDTO.class))).thenReturn(updated);

		mockMvc.perform(put("/owners/1000")
						.content(om.writeValueAsString(updated))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.lastName", is("Torres Updated")));
	}

	@Test
	public void testDeleteOwner() throws Exception {
		Mockito.doNothing().when(ownerService).delete(1000);

		mockMvc.perform(delete("/owners/1000"))
				.andExpect(status().isOk());
	}

	@Test
	public void testDeleteOwnerKO() throws Exception {
		Mockito.doThrow(new OwnerNotFoundException("Record not found...!")).when(ownerService).delete(1000);

		mockMvc.perform(delete("/owners/1000"))
				.andExpect(status().isNotFound());
	}
}
