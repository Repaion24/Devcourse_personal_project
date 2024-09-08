package com.grepp.coffee.model.service;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.exception.CustomDatabaseException;
import com.grepp.coffee.model.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;


    public List<OrderDTO> getOrdersByEmail(String email) {
        try {
            List<OrderDTO> OrderDTOList = orderRepository.getAllOrders(email);
            return OrderDTOList;
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }
}
