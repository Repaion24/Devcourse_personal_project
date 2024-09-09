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
    private static final String SUCCESS_MESSAGE = "Operation completed successfully.";

    @PutMapping
    public ResponseEntity<OrderDTO> createOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO createdOrder = orderService.createOrder(orderDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @GetMapping("/list")
    public ResponseEntity<List<OrderDTO>> getOrders(@RequestHeader("email") String email) {
        List<OrderDTO> orders = orderService.getOrdersByEmail(email);
        return handleOptionalResponse(orders);
    }

    @PutMapping("/mod")
    public ResponseEntity<OrderDTO> updateOrder(@RequestBody OrderDTO orderDTO) {
        OrderDTO updateOrder = orderService.updateOrder(orderDTO);
        return handleOptionalResponse(updateOrder);
    }

    @PutMapping("/moditem")
    public ResponseEntity<OrderItemDTO> updateOrderItem(@RequestBody OrderItemDTO orderItemDTO) {
        OrderItemDTO updateOrderItem = orderService.updateOrderItem(orderItemDTO);
        return handleOptionalResponse(updateOrderItem);
    }

    @DeleteMapping("/del")
    public ResponseEntity<?> deleteOrder(@RequestHeader("orderId")String orderId) {
        byte[] id = Base64.getDecoder().decode(orderId);
        int result = orderService.deleteOrder(id);
        return result == 0 ? ResponseEntity.noContent().build() : ResponseEntity.ok(SUCCESS_MESSAGE);
    }

    @DeleteMapping("/delItem")
    public ResponseEntity<?> deleteOrderItem(@RequestHeader("orderItemId")int orderItemId) {
        int result = orderService.deleteOrderItem(orderItemId);
        return result == 0 ? ResponseEntity.noContent().build() : ResponseEntity.ok(SUCCESS_MESSAGE);
    }

    private <T> ResponseEntity<T> handleOptionalResponse(T body) {
        if (body == null) {
            return ResponseEntity.noContent().build(); // 데이터가 없으면 204 반환
        }
        return ResponseEntity.ok(body);
    }
}
