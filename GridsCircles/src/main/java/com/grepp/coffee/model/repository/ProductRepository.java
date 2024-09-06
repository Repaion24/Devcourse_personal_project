package com.grepp.coffee.model.repository;

import com.grepp.coffee.model.dto.ProductDTO;
import org.apache.ibatis.annotations.Mapper;

import java.sql.SQLException;
import java.util.List;

@Mapper
public interface ProductRepository {
    List<ProductDTO> getProducts();
}
