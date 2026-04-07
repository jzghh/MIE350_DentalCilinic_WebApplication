package com.dentalclinic;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DentistControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void dentistCrudRoundTrip() throws Exception {
        String response = mockMvc.perform(post("/api/dentists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "firstName", "Test",
                                "lastName", "Dentist",
                                "specialization", "General Dentistry",
                                "phone", "416-555-9999",
                                "email", "test.dentist@clinic.com",
                                "workingDays", "Monday,Wednesday,Friday"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.dentistId").isNumber())
                .andExpect(jsonPath("$.firstName", is("Test")))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long dentistId = readJson(response).get("dentistId").asLong();

        mockMvc.perform(get("/api/dentists/{id}", dentistId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName", is("Dentist")));

        mockMvc.perform(put("/api/dentists/{id}", dentistId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "firstName", "Test",
                                "lastName", "Dentist",
                                "phone", "416-555-0000",
                                "specialization", "Orthodontics",
                                "email", "test.dentist@clinic.com",
                                "workingDays", "Monday,Wednesday,Friday"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone", is("416-555-0000")))
                .andExpect(jsonPath("$.specialization", is("Orthodontics")));

        mockMvc.perform(delete("/api/dentists/{id}", dentistId))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/dentists/{id}", dentistId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", containsString("not found")));
    }

    @Test
    void getAvailableDentistsReturnsList() throws Exception {
        mockMvc.perform(get("/api/dentists/available").param("date", "2026-03-06"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }
}
