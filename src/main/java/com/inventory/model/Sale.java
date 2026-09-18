package com.inventory.model;

import com.inventory.enums.PaymentMethod;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Sale {
    private String saleId;
    private String customerId;
    private LocalDateTime saleDate;
    private List<SaleItem> items;
    private double totalAmount;
    private PaymentMethod paymentMethod;

    public Sale() {
        this.items = new ArrayList<>();
        this.saleDate = LocalDateTime.now();
    }

    public Sale(String saleId, String customerId, LocalDateTime saleDate, PaymentMethod paymentMethod) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.saleDate = saleDate != null ? saleDate : LocalDateTime.now();
        this.items = new ArrayList<>();
        this.paymentMethod = paymentMethod;
        this.totalAmount = 0.0;
    }

    public Sale(String saleId, String customerId, LocalDateTime saleDate, List<SaleItem> items, PaymentMethod paymentMethod) {
        this.saleId = saleId;
        this.customerId = customerId;
        this.saleDate = saleDate != null ? saleDate : LocalDateTime.now();
        this.items = items != null ? items : new ArrayList<>();
        this.paymentMethod = paymentMethod;
        calculateTotalAmount();
    }

    public String getSaleId() {
        return saleId;
    }

    public void setSaleId(String saleId) {
        this.saleId = saleId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public LocalDateTime getSaleDate() {
        return saleDate;
    }

    public void setSaleDate(LocalDateTime saleDate) {
        this.saleDate = saleDate;
    }

    public List<SaleItem> getItems() {
        return items;
    }

    public void setItems(List<SaleItem> items) {
        this.items = items != null ? items : new ArrayList<>();
        calculateTotalAmount();
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void addItem(SaleItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        calculateTotalAmount();
    }

    public void removeItem(SaleItem item) {
        if (this.items != null) {
            this.items.remove(item);
            calculateTotalAmount();
        }
    }

    public double calculateTotalAmount() {
        double sum = 0.0;
        if (this.items != null) {
            for (SaleItem item : this.items) {
                if (item != null) {
                    sum += item.getSubtotal();
                }
            }
        }
        this.totalAmount = sum;
        return this.totalAmount;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "saleId='" + saleId + '\'' +
                ", customerId='" + customerId + '\'' +
                ", saleDate=" + saleDate +
                ", itemsCount=" + (items != null ? items.size() : 0) +
                ", totalAmount=" + totalAmount +
                ", paymentMethod=" + paymentMethod +
                '}';
    }
}
