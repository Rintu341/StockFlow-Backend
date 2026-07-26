package org.sujan.stockflow_backend.dto;

public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private double price;
    private Integer stockQuantity;

    public ProductResponse(Long id, String name, String category, double price, Integer stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.price = price;
        this.stockQuantity = stockQuantity;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public Integer getStockQuantity() {
        return stockQuantity;
    }
}