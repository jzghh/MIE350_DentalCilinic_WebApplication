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

class AppointmentControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void appointmentConflictReturnsHttp409() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "patientId", 10,
                                "dentistId", 1,
                                "appointmentDate", "2026-03-06",
                                "appointmentTime", "09:15:00",
                                "duration", 30,
                                "notes", "Conflict test"
                        ))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error", containsString("conflict")));
    }

    @Test
    void createAppointmentSucceedsWithNoConflict() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "patientId", 5,
                                "dentistId", 1,
                                "appointmentDate", "2026-06-15",
                                "appointmentTime", "14:00:00",
                                "duration", 30,
                                "notes", "New appointment test"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.appointmentId").isNumber())
                .andExpect(jsonPath("$.status", is("Scheduled")));
    }

    @Test
    void rescheduleAppointmentUpdatesTime() throws Exception {
        mockMvc.perform(put("/api/appointments/{id}", 4)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "appointmentTime", "15:00:00",
                                "notes", "Rescheduled"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notes", is("Rescheduled")));
    }

    @Test
    void deleteAppointmentReturns204() throws Exception {
        mockMvc.perform(delete("/api/appointments/{id}", 10))
                .andExpect(status().isNoContent());
    }

    @Test
    void getAppointmentsByPatientReturnsList() throws Exception {
        mockMvc.perform(get("/api/appointments/patient/{patientId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].patientId", is(1)));
    }
}
