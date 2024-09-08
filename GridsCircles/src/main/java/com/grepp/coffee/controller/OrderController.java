package com.grepp.coffee.controller;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.OrderItemDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;

    @PutMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderDTO>> getOrders(@RequestHeader("email") String email) {
        List<OrderDTO> orders = orderService.getOrdersByEmail(email);
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok(orders); // 200 OK와 함께 리스트 반환
    }

    @PutMapping("/mod")
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO updateOrder = orderService.updateOrder(orderDTO);
        if (updateOrder == null) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok(updateOrder); // 200 OK와 함께 리스트 반환
    }

    @PutMapping("/moditem")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO updateOrderItem = orderService.updateOrderItem(orderItemDTO);
        if (updateOrderItem == null) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok(updateOrderItem); // 200 OK와 함께 리스트 반환
    }

    @DeleteMapping("/del")
    public ResponseEntity<?> deleteOrder(@RequestHeader("orderId")String orderId) {
        byte[] id = Base64.getDecoder().decode(orderId);
        int result = orderService.deleteOrder(id);
        if (result == 0) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok("Order deleted successfully.");
    }

    @DeleteMapping("/delItem")
    public ResponseEntity<?> deleteOrderItem(@RequestHeader("orderItemId")int orderItemId) {
        int result = orderService.deleteOrderItem(orderItemId);
        if (result == 0) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok("OrderItem deleted successfully.");
    }
}
