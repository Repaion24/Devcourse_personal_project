package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderRepository {
    void insertOrder(Order order);
    List<OrderDTO> getOrdersByEmail(@Param("email")String email);
    OrderDTO getOrderByAll(OrderDTO order);
    String getOrderStatus(@Param("orderId")byte[] orderId);
    void updateOrder(Order order);
    int deleteOrder(@Param("orderId")byte[] orderId);
    List<OrderDTO> getAllOrders();
    int updateOrdersStatus(@Param("cutOffTime")LocalDateTime cutOffTime);
    List<OrderDTO> getOrdersByStatus(@Param("status")String status, @Param("cutOffTime")LocalDateTime cutOffTime);
    void updateOrderTimeById(@Param("id")byte[] id);
}
