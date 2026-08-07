package org.sujan.stockflow_backend.dto;

import org.sujan.stockflow_backend.entity.OrderItem;

public class OrderItemResponse {

    private Long productId;
    private String productName;
    private Integer quantity;

    public OrderItemResponse(OrderItem item) {
        this.productId = item.getProduct().getId();
        this.productName = item.getProduct().getName();
        this.quantity = item.getQuantity();
    }

    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
}