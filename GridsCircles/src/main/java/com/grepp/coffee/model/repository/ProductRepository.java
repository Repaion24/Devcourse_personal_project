package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.sql.SQLException;
import java.util.List;

@Mapper
public interface ProductRepository {
    List<ProductDTO> getAllProducts();
    List<ProductDTO> findByCategories(@Param("categories") List<String> categories);
}
