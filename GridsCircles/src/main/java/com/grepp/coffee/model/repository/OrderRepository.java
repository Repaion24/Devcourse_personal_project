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
    // INSERT ORDER
    void insertOrder(Order order);
    // SELECT ORDERS BY EMAIL
    List<OrderDTO> getOrdersByEmail(@Param("email")String email);
    // SELECT ORDER BY EMAIL, ADDRESS, POSTCODE
    OrderDTO getOrderByAll(OrderDTO order);
    // SELECT ORDER_STATUS BY ORDER_ID
    String getOrderStatus(@Param("orderId")byte[] orderId);
    // UPDATE ORDER
    void updateOrder(Order order);
    // DELETE ORDER BY ORDER_ID
    int deleteOrder(@Param("orderId")byte[] orderId);
    // SELECT ORDERS
    List<OrderDTO> getAllOrders();
    // UPDATE ORDER_STATUS
    int updateOrdersStatus(@Param("cutOffTime")LocalDateTime cutOffTime);
    // SELECT ORDERS BY ORDER_STATUS
    List<OrderDTO> getOrdersByStatus(@Param("status")String status, @Param("cutOffTime")LocalDateTime cutOffTime);
    // UPDATE UPDATE_AT BY ORDERS_ID
    void updateOrderTimeById(@Param("id")byte[] id);
}
