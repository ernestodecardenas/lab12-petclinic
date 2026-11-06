package com.tecsup.petclinic.webs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tecsup.petclinic.dtos.VisitDTO;
import com.tecsup.petclinic.exceptions.VisitNotFoundException;
import com.tecsup.petclinic.services.VisitService;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Pruebas de VisitController con VisitService mockeado.
 */
@AutoConfigureMockMvc
@SpringBootTest
@Slf4j
public class VisitControllerMockitoTest {

	private static final ObjectMapper om = new ObjectMapper();

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private VisitService visitService;

	private List<VisitDTO> filter(java.util.function.Predicate<VisitDTO> p) {
		return TObjectCreator.getAllVisitTOs().stream().filter(p).toList();
	}

	@Test
	public void testFindAllVisits() throws Exception {
		Mockito.when(visitService.findAll()).thenReturn(TObjectCreator.getAllVisitTOs());

		mockMvc.perform(get("/visits"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(6)))
				.andExpect(jsonPath("$[0].id", is(1)));
	}

	@Test
	public void testFindVisitOK() throws Exception {
		VisitDTO visitTO = TObjectCreator.getVisitTO();
		Mockito.when(visitService.findById(1)).thenReturn(visitTO);

		mockMvc.perform(get("/visits/1"))
				.andDo(print())
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.id", is(1)))
				.andExpect(jsonPath("$.description", is("rabies shot")));
	}

	@Test
	public void testFindVisitKO() throws Exception {
		Mockito.when(visitService.findById(666)).thenThrow(new VisitNotFoundException("Record not found...!"));

		mockMvc.perform(get("/visits/666"))
				.andExpect(status().isNotFound());
	}

	@Test
	public void testCreateVisit() throws Exception {
		VisitDTO created = TObjectCreator.newVisitCreatedTO();
		Mockito.when(visitService.create(any(VisitDTO.class))).thenReturn(created);

		mockMvc.perform(post("/visits")
						.content(om.writeValueAsString(TObjectCreator.newVisitTO()))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id", is(1000)))
				.andExpect(jsonPath("$.description", is(created.getDescription())));
	}

	@Test
	public void testUpdateVisit() throws Exception {
		VisitDTO updated = TObjectCreator.newVisitCreatedTO();
		updated.setDescription("vaccination - booster");
		Mockito.when(visitService.update(eq(1000), any(VisitDTO.class))).thenReturn(updated);

		mockMvc.perform(put("/visits/1000")
						.content(om.writeValueAsString(updated))
						.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.description", is("vaccination - booster")));
	}

	@Test
	public void testDeleteVisit() throws Exception {
		Mockito.doNothing().when(visitService).delete(1000);

		mockMvc.perform(delete("/visits/1000"))
				.andExpect(status().isOk());
	}

	@Test
	public void testFindByPetId() throws Exception {
		Mockito.when(visitService.findByPetId(7)).thenReturn(filter(v -> v.getPetId() == 7));

		mockMvc.perform(get("/visits").param("petId", "7"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)));
	}

	@Test
	public void testFindByVetId() throws Exception {
		Mockito.when(visitService.findByVetId(2)).thenReturn(filter(v -> v.getVetId() == 2));

		mockMvc.perform(get("/visits").param("vetId", "2"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].vetId", everyItem(is(2))));
	}

	@Test
	public void testCalculateTotalCostByPet() throws Exception {
		Mockito.when(visitService.calculateTotalCostByPet(7)).thenReturn(new BigDecimal("320.00"));

		mockMvc.perform(get("/visits/pet/7/total-cost"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalCost", is(320.0)));
	}

	@Test
	public void testFindByDateBetween() throws Exception {
		LocalDate from = LocalDate.of(2024, 1, 1);
		LocalDate to = LocalDate.of(2024, 12, 31);
		Mockito.when(visitService.findByDateBetween(from, to))
				.thenReturn(filter(v -> v.getVisitDate().startsWith("2024")));

		mockMvc.perform(get("/visits").param("from", "2024-01-01").param("to", "2024-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(2)))
				.andExpect(jsonPath("$[*].id", containsInAnyOrder(5, 6)));
	}

	@Test
	public void testFindByVetIdAndDateBetween() throws Exception {
		LocalDate from = LocalDate.of(2024, 1, 1);
		LocalDate to = LocalDate.of(2024, 12, 31);
		Mockito.when(visitService.findByVetIdAndDateBetween(1, from, to))
				.thenReturn(filter(v -> v.getId() == 5));

		mockMvc.perform(get("/visits").param("vetId", "1").param("from", "2024-01-01").param("to", "2024-12-31"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].id", is(5)));
	}

	@Test
	public void testCountVisitsByPet() throws Exception {
		Mockito.when(visitService.countVisitsByPet(8)).thenReturn(2L);

		mockMvc.perform(get("/visits/pet/8/count"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.count", is(2)));
	}
}
