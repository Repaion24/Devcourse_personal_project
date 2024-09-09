package com.grepp.coffee.model.service;

import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.exception.CustomDatabaseException;
import com.grepp.coffee.model.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.ByteBuffer;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

@Component
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        return executeWithExceptionHandling(() -> productRepository.getAllProducts());
    }

    public List<ProductDTO> getProductsByCategories(List<String> categories) {
        return executeWithExceptionHandling(() -> productRepository.findByCategories(categories));
    }
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        return executeWithExceptionHandling(() -> {
            productDTO.setProductId(uuidToBytes(UUID.randomUUID()));
            LocalDateTime now = LocalDateTime.now();
            productDTO.setCreatedAt(now);
            productDTO.setUpdatedAt(now);
            productRepository.addProduct(productDTO);
            return productDTO;
        });
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        return executeWithExceptionHandling(() -> {
            productDTO.setUpdatedAt(LocalDateTime.now());
            productRepository.updateProduct(productDTO);
            return productDTO;
        });
    }

    @Transactional
    public int deleteProduct(byte[] productId){
        return executeWithExceptionHandling(() -> {
            System.out.println(productId);
            return productRepository.deleteProduct(productId);
        });
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }

    private <T> T executeWithExceptionHandling(Supplier<T> action) {
        try {
            return action.get();
        } catch (DataAccessException e) {
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }
}
