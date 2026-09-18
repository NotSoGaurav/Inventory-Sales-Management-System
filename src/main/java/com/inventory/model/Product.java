package com.inventory.model;

import com.inventory.enums.StockStatus;

public class Product {
    private String productId;
    private String name;
    private String category;
    private double price;
    private int quantity;
    private int minStockThreshold;
    private String supplierId;

    public Product() {
    }

    public Product(String productId, String name, String category, double price, int quantity, int minStockThreshold, String supplierId) {
        this.productId = productId;
        this.name = name;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.minStockThreshold = minStockThreshold;
        this.supplierId = supplierId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinStockThreshold() {
        return minStockThreshold;
    }

    public void setMinStockThreshold(int minStockThreshold) {
        this.minStockThreshold = minStockThreshold;
    }

    public String getSupplierId() {
        return supplierId;
    }

    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public StockStatus getStockStatus() {
        if (quantity <= 0) {
            return StockStatus.OUT_OF_STOCK;
        } else if (quantity <= minStockThreshold) {
            return StockStatus.LOW_STOCK;
        } else {
            return StockStatus.IN_STOCK;
        }
    }

    @Override
    public String toString() {
        return "Product{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", minStockThreshold=" + minStockThreshold +
                ", supplierId='" + supplierId + '\'' +
                ", stockStatus=" + getStockStatus() +
                '}';
    }
}
