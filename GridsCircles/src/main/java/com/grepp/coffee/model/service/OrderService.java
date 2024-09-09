package com.grepp.coffee.model.service;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.OrderItemDTO;
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
import java.util.function.Supplier;

@Component
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;


    public List<OrderDTO> getAllOrderList() {
        return executeWithExceptionHandling(() -> {
            List<OrderDTO> orderDTOList = orderRepository.getAllOrders();
            return enrichOrdersWithItems(orderDTOList);
        });
    }

    @Transactional
    public List<OrderDTO> updateOrdersStatusList(){
        return executeWithExceptionHandling(() -> {
            LocalDateTime cutOffTime = calculateCutOffTime();
            List<OrderDTO> orderDTOList = orderRepository.getOrdersByStatus("주문중", cutOffTime);
            orderRepository.updateOrdersStatus(cutOffTime);
            return enrichOrdersWithItems(orderDTOList);
        });
    }


    public List<OrderDTO> getOrdersByEmail(String email) {
        return executeWithExceptionHandling(() -> {
            List<OrderDTO> orderDTOList = orderRepository.getOrdersByEmail(email);
            return enrichOrdersWithItems(orderDTOList);
        });
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        return executeWithExceptionHandling(() -> {
            OrderDTO existingOrder = orderRepository.getOrderByAll(orderDTO);
            if (existingOrder != null) {
                orderDTO.setOrderId(existingOrder.getOrderId());
                updatetime(orderDTO.getOrderId());
                return createOrderItems(orderDTO);
            }
            orderDTO.setOrderId(uuidToBytes(UUID.randomUUID()));
            Order order = new Order(
                    orderDTO.getOrderId(),
                    orderDTO.getEmail(),
                    orderDTO.getAddress(),
                    orderDTO.getPostcode(),
                    "주문중",
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            orderRepository.insertOrder(order);
            return createOrderItems(orderDTO);
        });
    }

    @Transactional
    public OrderDTO updateOrder(OrderDTO orderDTO){
        return executeWithExceptionHandling(() -> {
            String orderStatus = getOrderStatus(orderDTO.getOrderId());
            if (orderDTO.getOrderId() == null || !"주문중".equals(orderStatus)) {
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
        });
    }

    @Transactional
    public OrderItemDTO updateOrderItem(OrderItemDTO orderItemDTO){
        return executeWithExceptionHandling(() -> {
            String orderStatus = getOrderStatus(orderItemDTO.getOrderId());
            if (orderItemDTO.getOrderId() == null || !"주문중".equals(orderStatus)) {
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
        });
    }

    @Transactional
    public int deleteOrder(byte[] orderId){
        return executeWithExceptionHandling(() -> orderRepository.deleteOrder(orderId));
    }

    @Transactional
    public int deleteOrderItem(int orderItemId){
        return executeWithExceptionHandling(() -> orderItemRepository.deleteOrderItemById(orderItemId));
    }


    private OrderDTO createOrderItems(OrderDTO orderDTO) {
        for (OrderItemDTO orderItemDTO : orderDTO.getOrderItemDTOList()) {
            OrderItem orderItem = new OrderItem(
                    orderDTO.getOrderId(),
                    orderItemDTO.getProductId(),
                    orderItemDTO.getCategory(),
                    orderItemDTO.getPrice(),
                    orderItemDTO.getQuantity(),
                    LocalDateTime.now(),
                    LocalDateTime.now()
            );
            orderItemRepository.insertOrderItem(orderItem);
        }
        return orderDTO;
    }

    private OrderDTO getOrderByEmail(OrderDTO orderDTO){
        return orderRepository.getOrderByAll(orderDTO);
    }

    private String getOrderStatus(byte[] id){
        return orderRepository.getOrderStatus(id);
    }

    private LocalDateTime calculateCutOffTime() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutOffTime = now.withHour(14).withMinute(0).withSecond(0).withNano(0);
        if (now.isBefore(cutOffTime)) {
            cutOffTime = cutOffTime.minusDays(1);
        }
        return cutOffTime;
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }


    private void updatetime(byte[] id) {
        orderRepository.updateOrderTimeById(id);
    }


    private <T> T executeWithExceptionHandling(Supplier<T> action) {
        try {
            return action.get();
        } catch (DataAccessException e) {
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    private List<OrderDTO> enrichOrdersWithItems(List<OrderDTO> orderDTOList) {
        for (OrderDTO orderDTO : orderDTOList) {
            List<OrderItemDTO> orderItemDTOList = orderItemRepository.getOrderItemsByOrderId(orderDTO.getOrderId());
            orderDTO.setOrderItemDTOList(orderItemDTOList);
        }
        return orderDTOList;
    }
}
