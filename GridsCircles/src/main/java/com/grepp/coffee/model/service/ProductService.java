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

@Component
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        try {
            List<ProductDTO> productDTOList = productRepository.getAllProducts();
            return productDTOList;
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    public List<ProductDTO> getProductsByCategories(List<String> categories) {
        try {
            List<ProductDTO> productDTOList = productRepository.findByCategories(categories);
            return productDTOList;
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }
    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        try {
            productDTO.setProductId(uuidToBytes(UUID.randomUUID()));
            productDTO.setCreatedAt(LocalDateTime.now());
            productDTO.setUpdatedAt(LocalDateTime.now());
            productRepository.addProduct(productDTO);
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
        return productDTO;
    }

    @Transactional
    public ProductDTO updateProduct(ProductDTO productDTO) {
        try {
            productDTO.setUpdatedAt(LocalDateTime.now());
            productRepository.updateProduct(productDTO);
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
        return productDTO;
    }

    @Transactional
    public int deleteProduct(byte[] productId){
        System.out.println(productId);
        return productRepository.deleteOrder(productId);
    }

    private static byte[] uuidToBytes(UUID uuid) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[16]);
        byteBuffer.putLong(uuid.getMostSignificantBits());
        byteBuffer.putLong(uuid.getLeastSignificantBits());
        return byteBuffer.array();
    }
}
