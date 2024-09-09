package com.grepp.coffee.controller;

import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/menu")
public class ProductController {
    @Autowired
    private ProductService productService;
    private static final String NO_CONTENT_MESSAGE = "No products found.";


    @GetMapping("/list")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return handleResponse(products);
    }


    @GetMapping("/{category}")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(@PathVariable("category") String category) {
        List<String> categories = Arrays.asList(category.split(","));
        List<ProductDTO> products = productService.getProductsByCategories(categories);
        return handleResponse(products);
    }

    private <T> ResponseEntity<List<T>> handleResponse(List<T> list) {
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok(list); // 리스트가 있으면 200 OK
    }
}
