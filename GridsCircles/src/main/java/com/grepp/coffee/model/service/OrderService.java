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


    /**
     * 모든 주문을 가져오는 메서드
     */
    public List<OrderDTO> getAllOrderList() {
        return executeWithExceptionHandling(() -> {
            List<OrderDTO> orderDTOList = orderRepository.getAllOrders();
            return enrichOrdersWithItems(orderDTOList);
        });
    }

    /**
     * 주문 상태를 업데이트하는 메서드
     *  주문중 -> 배송중
     */
    @Transactional
    public List<OrderDTO> updateOrdersStatusList(){
        return executeWithExceptionHandling(() -> {
            LocalDateTime cutOffTime = calculateCutOffTime();
            List<OrderDTO> orderDTOList = orderRepository.getOrdersByStatus("주문중", cutOffTime);
            orderRepository.updateOrdersStatus(cutOffTime);
            return enrichOrdersWithItems(orderDTOList);
        });
    }

    /**
     * 이메일로 주문을 가져오는 메서드
     */
    public List<OrderDTO> getOrdersByEmail(String email) {
        return executeWithExceptionHandling(() -> {
            List<OrderDTO> orderDTOList = orderRepository.getOrdersByEmail(email);
            return enrichOrdersWithItems(orderDTOList);
        });
    }

    /**
     * 주문을 생성하는 메서드
     * 이미 동일한 주문이 존재한다면,
     * Order를 새로 생성하지 않고, OrderItems만 추가
     */
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

    /**
     * 주문을 업데이트하는 메서드
     */
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

    /**
     * 주문 항목을 업데이트하는 메서드
     */
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

    /**
     * 주문을 삭제하는 메서드
     */
    @Transactional
    public int deleteOrder(byte[] orderId){
        return executeWithExceptionHandling(() -> orderRepository.deleteOrder(orderId));
    }

    /**
     * 주문 항목을 삭제하는 메서드
     */
    @Transactional
    public int deleteOrderItem(int orderItemId){
        return executeWithExceptionHandling(() -> orderItemRepository.deleteOrderItemById(orderItemId));
    }

    /**
     * 주문 항목을 생성하고, 저장하는 매서드
     */
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

    /**
     * 특정 주문 ID에 대한 상태 정보를 조회하는 메서드
     */
    private String getOrderStatus(byte[] id){
        return orderRepository.getOrderStatus(id);
    }

    /**
     * 오후 2시 기준의 시간을 계산하는 메서드
     */
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

    /**
     * 주문의 마지막 업데이트 시간을 갱신하는 메서드
     */
    private void updatetime(byte[] id) {
        orderRepository.updateOrderTimeById(id);
    }

    // 예외 처리 메서드
    private <T> T executeWithExceptionHandling(Supplier<T> action) {
        try {
            return action.get();
        } catch (DataAccessException e) {
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    /**
     * 주문 목록에 각 주문의 주문 항목들을 추가하는 메서드
     */
    private List<OrderDTO> enrichOrdersWithItems(List<OrderDTO> orderDTOList) {
        for (OrderDTO orderDTO : orderDTOList) {
            List<OrderItemDTO> orderItemDTOList = orderItemRepository.getOrderItemsByOrderId(orderDTO.getOrderId());
            orderDTO.setOrderItemDTOList(orderItemDTOList);
        }
        return orderDTOList;
    }
}
