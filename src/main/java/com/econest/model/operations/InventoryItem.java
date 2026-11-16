package com.econest.model.operations;
public record InventoryItem(int productId, String productName, String category, int quantity, int reorderLevel) {
    public boolean isLowStock() { return quantity <= reorderLevel; }
}
