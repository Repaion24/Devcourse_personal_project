package com.grepp.coffee.model.service;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.OrderItemDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.entity.Order;
import com.grepp.coffee.model.entity.OrderItem;
import com.grepp.coffee.model.exception.CustomDatabaseException;
import com.grepp.coffee.model.repository.OrderItemRepository;
import com.grepp.coffee.model.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Component
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    public List<OrderDTO> getOrdersByEmail(String email) {
        try {
            List<OrderDTO> orderDTOList = orderRepository.getAllOrders(email);
            for (OrderDTO orderDTO : orderDTOList) {
                List<OrderItemDTO> orderItemDTOList = orderItemRepository.getOrderItemsByOrderId(orderDTO.getOrderId());
                orderDTO.setOrderItemDTOList(orderItemDTOList);
            }
            return orderDTOList;
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            Order order = new Order(
                    uuidToBytes(UUID.randomUUID()),
                    orderDTO.getEmail(),
                    orderDTO.getAddress(),
                    orderDTO.getPostcode(),
                    "주문중",
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            orderRepository.insertOrder(order);


            for(OrderItemDTO orderItemDTO : orderDTO.getOrderItemDTOList()){
                OrderItem orderItem = new OrderItem(
                        order.getOrderId(),
                        orderItemDTO.getProductId(),
                        orderItemDTO.getCategory(),
                        orderItemDTO.getPrice(),
                        orderItemDTO.getQuantity(),
                        LocalDateTime.now(),
                        LocalDateTime.now()
                );
                orderItemRepository.insertOrderItem(orderItem);
            }
            return order.toOrderDTO();
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO){
        try {
            String orderStatus = getOrderStatus(orderDTO.getOrderId());
        if(orderDTO.getOrderId() == null || !"주문중".equals(orderStatus)){
            return null;
        }
        Order order = new Order(
                orderDTO.getOrderId(),
                orderDTO.getEmail(),
                orderDTO.getAddress(),
                orderDTO.getPostcode(),
                orderDTO.getOrderStatus(),
                orderDTO.getCreatedAt(),
                LocalDateTime.now()
        );
        orderRepository.updateOrder(order);
        return order.toOrderDTO();
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    @Transactional
    public OrderItemDTO updateOrderItem(OrderItemDTO orderItemDTO){
        try {
            String orderStatus = getOrderStatus(orderItemDTO.getOrderId());
            if(orderItemDTO.getOrderId() == null || !"주문중".equals(orderStatus)){
                return null;
            }
            OrderItem orderItem = new OrderItem(
                    orderItemDTO.getOrderId(),
                    orderItemDTO.getProductId(),
                    orderItemDTO.getCategory(),
                    orderItemDTO.getPrice(),
                    orderItemDTO.getQuantity(),
                    orderItemDTO.getCreatedAt(),
                    LocalDateTime.now()
            );
            orderItem.setSeq(orderItemDTO.getSeq());
            orderItemRepository.updateOrderItemById(orderItem);
            return orderItem.toOrderItemDTO();
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    @Transactional
    public int deleteOrder(byte[] orderId){
        System.out.println(orderId);
        return orderRepository.deleteOrder(orderId);
    }

    @Transactional
    public int deleteOrderItem(int orderItemId){
        return orderItemRepository.deleteOrderItemById(orderItemId);
    }




    private String getOrderStatus(byte[] id){
        return orderRepository.getOrderStatus(id);
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }
}
