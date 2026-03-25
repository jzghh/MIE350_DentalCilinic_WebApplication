package com.dentalclinic.dto;

public class InventoryAdjustmentDTO {
    private Integer quantity;
    private String supplier;
    private String reason;

    // Getters and Setters
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public String getSupplier() { return supplier; }
    public void setSupplier(String supplier) { this.supplier = supplier; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}