package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.OrderItemDTO;
import com.grepp.coffee.model.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemRepository {
    // ORDER_ITEMS INSERT
    void insertOrderItem(OrderItem orderItem);
    // SELECT ORDER_ITEMS BY ORDER_ID
    List<OrderItemDTO> getOrderItemsByOrderId(@Param("orderId")byte[] orderId);
    // UPDATE ORDER_ITEMS BY ORDER_ID
    void updateOrderItemById(OrderItem orderItem);
    // DELETE ORDER_ITEMS BY ORDER_ID
    int deleteOrderItemById(@Param("orderId")int orderId);
}
