package com.grepp.coffee.controller;

import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/coffee")
public class ProductController {
    @Autowired
    private ProductService productService;


    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok(products); // 200 OK와 함께 리스트 반환
    }
}
