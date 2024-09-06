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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> getAllProducts() {
        try {
            List<ProductDTO> productDTOList = productRepository.getProducts();




            return productDTOList;
        } catch (DataAccessException e){
            System.err.println("Database access error: " + e.getMessage());
            throw new CustomDatabaseException("Database access error", e);
        }
    }

    private static UUID bytesToUUID(byte[] bytes) {
        ByteBuffer byteBuffer = ByteBuffer.wrap(bytes);
        long mostSigBits = byteBuffer.getLong(); // 상위 64비트
        long leastSigBits = byteBuffer.getLong(); // 하위 64비트
        return new UUID(mostSigBits, leastSigBits);
    }
}
