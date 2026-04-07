package com.dentalclinic;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ServiceControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void createServiceForVisitAndRetrieve() throws Exception {
        String response = mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "visitId", 1,
                                "serviceName", "Dental X-Ray",
                                "description", "Full mouth X-ray",
                                "unitCost", 75.0,
                                "quantity", 2
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.serviceId").isNumber())
                .andExpect(jsonPath("$.serviceName", is("Dental X-Ray")))
                .andExpect(jsonPath("$.unitCost", is(75.0)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        mockMvc.perform(get("/api/services/visit/{visitId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));
    }

    @Test
    void deleteServiceReturns204() throws Exception {
        String response = mockMvc.perform(post("/api/services")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "visitId", 2,
                                "serviceName", "Temporary Service",
                                "unitCost", 10.0,
                                "quantity", 1
                        ))))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        long serviceId = readJson(response).get("serviceId").asLong();

        mockMvc.perform(delete("/api/services/{id}", serviceId))
                .andExpect(status().isNoContent());
    }
}
