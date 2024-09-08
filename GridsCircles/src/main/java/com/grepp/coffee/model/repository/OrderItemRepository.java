package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.OrderItemDTO;
import com.grepp.coffee.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemRepository {
    void insertOrderItem(OrderItem orderItem);
    List<OrderItemDTO> getOrderItemsByOrderId(@Param("orderId")byte[] orderId);
    void updateOrderItemById(OrderItem orderItem);
    int deleteOrderItemById(@Param("orderId")int orderId);
}
