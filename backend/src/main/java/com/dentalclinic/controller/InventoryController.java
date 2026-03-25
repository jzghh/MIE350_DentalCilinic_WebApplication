package com.dentalclinic.controller;

import com.dentalclinic.dto.InventoryRequestDTO;
import com.dentalclinic.dto.InventoryResponseDTO;
import com.dentalclinic.dto.InventoryAdjustmentDTO;
import com.dentalclinic.dto.InventoryLogResponseDTO;
import com.dentalclinic.model.Inventory;
import com.dentalclinic.model.InventoryLog;
import com.dentalclinic.service.InventoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping
    public List<InventoryResponseDTO> getAll() {
        return service.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(convertToResponseDTO(service.findById(id)));
    }

    @GetMapping("/low-stock")
    public List<InventoryResponseDTO> getLowStock() {
        return service.findLowStock().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ResponseEntity<InventoryResponseDTO> create(@RequestBody InventoryRequestDTO dto) {
        Inventory item = convertToEntity(dto);
        return new ResponseEntity<>(convertToResponseDTO(service.create(item)), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<InventoryResponseDTO> update(@PathVariable Long id, @RequestBody InventoryRequestDTO dto) {
        Inventory item = convertToEntity(dto);
        return ResponseEntity.ok(convertToResponseDTO(service.update(id, item)));
    }

    @PutMapping("/{id}/restock")
    public ResponseEntity<InventoryResponseDTO> restock(@PathVariable Long id, @RequestBody InventoryAdjustmentDTO dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        Inventory updated = service.restock(id, dto.getQuantity(), dto.getSupplier());
        return ResponseEntity.ok(convertToResponseDTO(updated));
    }

    @PutMapping("/{id}/consume")
    public ResponseEntity<InventoryResponseDTO> consume(@PathVariable Long id, @RequestBody InventoryAdjustmentDTO dto) {
        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }
        Inventory updated = service.consume(id, dto.getQuantity(), dto.getReason());
        return ResponseEntity.ok(convertToResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/history")
    public List<InventoryLogResponseDTO> getHistory(@RequestParam(required = false) Long itemId,
                                                    @RequestParam(required = false) String startDate,
                                                    @RequestParam(required = false) String endDate) {
        List<InventoryLog> logs;
        if (itemId != null) {
            logs = service.getHistoryByItem(itemId);
        } else if (startDate != null && endDate != null) {
            logs = service.getHistoryByDateRange(
                    LocalDateTime.parse(startDate + "T00:00:00"),
                    LocalDateTime.parse(endDate + "T23:59:59"));
        } else {
            logs = service.getAllHistory();
        }
        return logs.stream().map(this::convertToLogResponseDTO).collect(Collectors.toList());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleError(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("error", ex.getMessage()));
    }

    private Inventory convertToEntity(InventoryRequestDTO dto) {
        Inventory inv = new Inventory();
        inv.setItemName(dto.getItemName());
        inv.setCategory(dto.getCategory());
        inv.setQuantity(dto.getQuantity() != null ? dto.getQuantity() : 0);
        inv.setUnitPrice(dto.getUnitPrice());
        inv.setReorderLevel(dto.getReorderLevel() != null ? dto.getReorderLevel() : 10);
        inv.setSupplier(dto.getSupplier());
        return inv;
    }

    private InventoryResponseDTO convertToResponseDTO(Inventory inv) {
        InventoryResponseDTO dto = new InventoryResponseDTO();
        dto.setItemId(inv.getItemId());
        dto.setItemName(inv.getItemName());
        dto.setCategory(inv.getCategory());
        dto.setQuantity(inv.getQuantity());
        dto.setUnitPrice(inv.getUnitPrice());
        dto.setReorderLevel(inv.getReorderLevel());
        dto.setSupplier(inv.getSupplier());
        dto.setLastRestocked(inv.getLastRestocked());

        dto.setLowStock(inv.getQuantity() != null && inv.getReorderLevel() != null
                && inv.getQuantity() <= inv.getReorderLevel());
        return dto;
    }

    private InventoryLogResponseDTO convertToLogResponseDTO(InventoryLog log) {
        InventoryLogResponseDTO dto = new InventoryLogResponseDTO();
        dto.setLogId(log.getLogId());
        dto.setItemId(log.getItemId());
        dto.setChangeType(log.getChangeType());
        dto.setQuantityChanged(log.getQuantityChanged());
        dto.setPreviousQty(log.getPreviousQty());
        dto.setNewQty(log.getNewQty());
        dto.setReason(log.getReason());
        dto.setChangedAt(log.getChangedAt());

        if (log.getInventory() != null) {
            dto.setItemName(log.getInventory().getItemName());
        }
        return dto;
    }
}