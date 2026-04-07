package com.dentalclinic;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;

import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class BillingControllerIntegrationTest extends AbstractIntegrationTest {

    @Test
    void createBillingDefaultsToPending() throws Exception {
        String response = mockMvc.perform(post("/api/billing")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "patientId", 1,
                                "totalAmount", 200.0
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.billId").isNumber())
                .andExpect(jsonPath("$.paymentStatus", is("Pending")))
                .andExpect(jsonPath("$.amountPaid", is(0.0)))
                .andReturn()
                .getResponse()
                .getContentAsString();

        long billId = readJson(response).get("billId").asLong();

        mockMvc.perform(get("/api/billing/{id}", billId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalAmount", is(200.0)))
                .andExpect(jsonPath("$.patientId", is(1)));
    }

    @Test
    void updateBillingPaymentStatusToPaid() throws Exception {
        mockMvc.perform(put("/api/billing/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(Map.of(
                                "paymentStatus", "Paid",
                                "amountPaid", 150.0,
                                "paymentMethod", "Cash"
                        ))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paymentStatus", is("Paid")))
                .andExpect(jsonPath("$.amountPaid", is(150.0)))
                .andExpect(jsonPath("$.paymentMethod", is("Cash")));
    }

    @Test
    void getBillingByPatientReturnsList() throws Exception {
        mockMvc.perform(get("/api/billing/patient/{patientId}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].patientId", is(1)));
    }

    @Test
    void getOverdueBillingsReturnsList() throws Exception {
        mockMvc.perform(get("/api/billing/overdue"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
}
