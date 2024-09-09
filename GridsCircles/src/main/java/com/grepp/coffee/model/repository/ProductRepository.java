package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

@Mapper
public interface ProductRepository {
    // SELECT PRODUCTS
    List<ProductDTO> getAllProducts();
    // SELECT PRODUCTS BY CATEGORY
    List<ProductDTO> findByCategories(@Param("categories") List<String> categories);
    // INSERT PRODUCT
    void addProduct(ProductDTO product);
    // UPDATE PRODUCT
    void updateProduct(ProductDTO productDTO);
    // DELETE PRODUCT BY PRODUCT_ID
    int deleteProduct(@Param("productId")byte[] productId);
}
