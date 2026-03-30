package com.dentalclinic.controller;

import com.dentalclinic.dto.BillingRequestDTO;
import com.dentalclinic.dto.BillingResponseDTO;
import com.dentalclinic.model.Billing;
import com.dentalclinic.service.BillingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/billing")
public class BillingController {

    private final BillingService service;

    public BillingController(BillingService service) {
        this.service = service;
    }

    @GetMapping
    public List<BillingResponseDTO> getAll() {
        return service.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BillingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(convertToResponseDTO(service.findById(id)));
    }

    @GetMapping("/overdue")
    public List<BillingResponseDTO> getOverdue() {
        return service.findOverdue().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/patient/{patientId}")
    public List<BillingResponseDTO> getByPatient(@PathVariable Long patientId) {
        return service.findByPatientId(patientId).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<BillingResponseDTO> create(@RequestBody BillingRequestDTO dto) {
        Billing billing = convertToEntity(dto);
        return new ResponseEntity<>(convertToResponseDTO(service.create(billing)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BillingResponseDTO> update(@PathVariable Long id, @RequestBody BillingRequestDTO dto) {
        Billing billing = convertToEntity(dto);
        return ResponseEntity.ok(convertToResponseDTO(service.update(id, billing)));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    private Billing convertToEntity(BillingRequestDTO dto) {
        Billing billing = new Billing();
        billing.setPatientId(dto.getPatientId());
        billing.setAppointmentId(dto.getAppointmentId());
        billing.setTotalAmount(dto.getTotalAmount());
        billing.setAmountPaid(dto.getAmountPaid() != null ? dto.getAmountPaid() : 0.0);
        billing.setPaymentMethod(dto.getPaymentMethod());
        billing.setPaymentStatus(dto.getPaymentStatus());
        billing.setBillingDate(dto.getBillingDate());
        return billing;
    }

    private BillingResponseDTO convertToResponseDTO(Billing billing) {
        BillingResponseDTO dto = new BillingResponseDTO();
        dto.setBillId(billing.getBillId());
        dto.setPatientId(billing.getPatientId());
        dto.setAppointmentId(billing.getAppointmentId());
        dto.setTotalAmount(billing.getTotalAmount());
        dto.setAmountPaid(billing.getAmountPaid() != null ? billing.getAmountPaid() : 0.0);
        dto.setPaymentMethod(billing.getPaymentMethod());
        dto.setPaymentStatus(billing.getPaymentStatus());
        dto.setBillingDate(billing.getBillingDate());

        double total = billing.getTotalAmount() != null ? billing.getTotalAmount() : 0.0;
        double paid = billing.getAmountPaid() != null ? billing.getAmountPaid() : 0.0;
        dto.setBalanceDue(total - paid);

        if (billing.getPatient() != null) {
            dto.setPatientName(billing.getPatient().getFirstName() + " " + billing.getPatient().getLastName());
        }

        return dto;
    }
}