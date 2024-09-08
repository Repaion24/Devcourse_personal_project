package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderRepository {
    void insertOrder(Order order);
    List<OrderDTO> getAllOrders(@Param("email")String email);
    String getOrderStatus(@Param("orderId")byte[] orderId);
    void updateOrder(Order order);
    int deleteOrder(@Param("orderId")byte[] orderId);
}
