package com.grepp.coffee.controller;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private ProductService productService;


    @PostMapping("/reg")
    public ResponseEntity<ProductDTO> register(@RequestBody ProductDTO productDTO) {
        ProductDTO createProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(createProduct); // 200 OK와 함께 리스트 반환
    }

    @PutMapping("/mod")
    public ResponseEntity<ProductDTO> update(@RequestBody ProductDTO productDTO) {
        ProductDTO updateProduct = productService.updateProduct(productDTO);
        return ResponseEntity.ok(updateProduct);
    }

    @DeleteMapping("/del")
    public ResponseEntity<?> delete(@RequestHeader("productId")String productId) {
        byte[] id = Base64.getDecoder().decode(productId);
        int result = productService.deleteProduct(id);
        if (result == 0) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok("Product deleted successfully.");
    }
}
