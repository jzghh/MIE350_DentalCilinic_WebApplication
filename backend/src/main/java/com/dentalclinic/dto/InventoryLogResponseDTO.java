package com.dentalclinic.dto;

import java.time.LocalDateTime;

public class InventoryLogResponseDTO {
    private Long logId;
    private Long itemId;
    private String itemName;
    private String changeType;
    private Integer quantityChanged;
    private Integer previousQty;
    private Integer newQty;
    private String reason;
    private LocalDateTime changedAt;

    // Getters and Setters
    public Long getLogId() { return logId; }
    public void setLogId(Long logId) { this.logId = logId; }
    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }
    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
    public String getChangeType() { return changeType; }
    public void setChangeType(String changeType) { this.changeType = changeType; }
    public Integer getQuantityChanged() { return quantityChanged; }
    public void setQuantityChanged(Integer quantityChanged) { this.quantityChanged = quantityChanged; }
    public Integer getPreviousQty() { return previousQty; }
    public void setPreviousQty(Integer previousQty) { this.previousQty = previousQty; }
    public Integer getNewQty() { return newQty; }
    public void setNewQty(Integer newQty) { this.newQty = newQty; }
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}