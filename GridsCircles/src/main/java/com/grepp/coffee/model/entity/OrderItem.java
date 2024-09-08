package com.grepp.coffee.model.entity;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.OrderItemDTO;

import java.time.LocalDateTime;
import java.util.Arrays;

public class OrderItem {
    private int seq;
    private byte[] orderId;
    private byte[] productId;
    private String category;
    private long price;
    private int quantity;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public OrderItem() {}

    public OrderItem(byte[] orderId, byte[] productId, String category, long price, int quantity, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.orderId = orderId;
        this.productId = productId;
        this.category = category;
        this.price = price;
        this.quantity = quantity;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public OrderItemDTO toOrderItemDTO(){
        OrderItemDTO dto = new OrderItemDTO();
        dto.setSeq(seq);
        dto.setOrderId(orderId);
        dto.setProductId(productId);
        dto.setCategory(category);
        dto.setPrice(price);
        dto.setQuantity(quantity);
        dto.setCreatedAt(createdAt);
        dto.setUpdatedAt(updatedAt);
        return dto;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public byte[] getOrderId() {
        return orderId;
    }

    public void setOrderId(byte[] orderId) {
        this.orderId = orderId;
    }

    public byte[] getProductId() {
        return productId;
    }

    public void setProductId(byte[] productId) {
        this.productId = productId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "orderId=" + Arrays.toString(orderId) +
                ", productId=" + Arrays.toString(productId) +
                ", category='" + category + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}
