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


    public List<OrderDTO> getAllOrders() {
        try{
            List<OrderDTO> orderDTOList = orderRepository.getAllOrders();
            return getOrderItemsByOrderId(orderDTOList);
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    @Transactional
    public List<OrderDTO> updateOrdersStatus(){
        try{
            // 기준 시간을 오후 2시로 설정
            // 요청 시점이 오후 2시 이전이면, 전날 오후 2시 이전의 주문만 업데이트
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime cutOffTime = now.withHour(14).withMinute(0).withSecond(0).withNano(0);
            if (now.isBefore(cutOffTime)) {
                cutOffTime = cutOffTime.minusDays(1); // 전날 오후 2시
            }
            List<OrderDTO> orderDTOList = orderRepository.getOrdersByStatus("주문중", cutOffTime);
            orderRepository.updateOrdersStatus(cutOffTime);
            return getOrderItemsByOrderId(orderDTOList);
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }


    public List<OrderDTO> getOrdersByEmail(String email) {
        try {
            List<OrderDTO> orderDTOList = orderRepository.getOrdersByEmail(email);
            return getOrderItemsByOrderId(orderDTOList);
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    @Transactional
    public OrderDTO createOrder(OrderDTO orderDTO) {
        try {
            OrderDTO getOrder = getOrderByEmail(orderDTO);
            if (getOrder != null) {
                orderDTO.setOrderId(getOrder.getOrderId());
                updatetime(orderDTO.getOrderId());
                return createOrderItemDTO(orderDTO);
            }
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


    private OrderDTO createOrderItemDTO(OrderDTO orderDTO) {
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

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    private List<OrderDTO> getOrderItemsByOrderId(List<OrderDTO> orderDTOList){
        for (OrderDTO orderDTO : orderDTOList) {
            List<OrderItemDTO> orderItemDTOList = orderItemRepository.getOrderItemsByOrderId(orderDTO.getOrderId());
            orderDTO.setOrderItemDTOList(orderItemDTOList);
        }
        return orderDTOList;
    }

    private void updatetime(byte[] id){
        orderRepository.updateOrderTimeById(id);
    }
}
