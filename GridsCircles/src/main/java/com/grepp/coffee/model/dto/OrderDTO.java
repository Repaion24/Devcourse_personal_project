package com.grepp.coffee.model.dto;

import com.grepp.coffee.model.entity.Order;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class OrderDTO {
    private byte[] orderId;
    private String email;
    private String address;
    private String postcode;
    private String orderStatus;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<OrderItemDTO> orderItemDTOList;

    public OrderDTO() {}

    public OrderDTO(byte[] orderId, String email, String address, String postcode, String orderStatus, LocalDateTime createdAt, LocalDateTime updatedAt, List<OrderItemDTO> orderItemDTOList) {
        this.orderId = orderId;
        this.email = email;
        this.address = address;
        this.postcode = postcode;
        this.orderStatus = orderStatus;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.orderItemDTOList = orderItemDTOList;
    }

    public Order toOrder(){
        Order order = new Order();
        order.setOrderId(orderId);
        order.setEmail(email);
        order.setAddress(address);
        order.setPostcode(postcode);
        order.setOrderStatus(orderStatus);
        order.setCreatedAt(createdAt);
        order.setUpdatedAt(updatedAt);
        return order;
    }

    public byte[] getOrderId() {
        return orderId;
    }

    public void setOrderId(byte[] orderId) {
        this.orderId = orderId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
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

    public List<OrderItemDTO> getOrderItemDTOList() {
        return orderItemDTOList;
    }

    public void setOrderItemDTOList(List<OrderItemDTO> orderItemDTOList) {
        this.orderItemDTOList = orderItemDTOList;
    }

    @Override
    public String toString() {
        return "OrderDTO{" +
                "orderId=" + Arrays.toString(orderId) +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                ", postcode='" + postcode + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", orderItemDTOList=" + orderItemDTOList +
                '}';
    }
}
