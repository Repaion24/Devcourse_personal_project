package com.grepp.coffee.controller;

import com.grepp.coffee.model.dto.OrderDTO;
import com.grepp.coffee.model.dto.ProductDTO;
import com.grepp.coffee.model.service.OrderService;
import com.grepp.coffee.model.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {
    public static final String REGISTER_PATH = "/reg";
    public static final String UPDATE_PATH = "/mod";
    public static final String DELETE_PATH = "/del";
    public static final String LIST_PATH = "/list";
    public static final String DELIVER_PATH = "/deli";

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderService orderService;


    @PostMapping(REGISTER_PATH)
    public ResponseEntity<ProductDTO> register(@RequestBody ProductDTO productDTO) {
        ProductDTO createProduct = productService.createProduct(productDTO);
        return ResponseEntity.ok(createProduct);    // 200 OK와 함께 추가한 상품 반환
    }

    @PutMapping(UPDATE_PATH)
    public ResponseEntity<ProductDTO> update(@RequestBody ProductDTO productDTO) {
        ProductDTO updateProduct = productService.updateProduct(productDTO);
        return ResponseEntity.ok(updateProduct);    // 200 OK와 함께 업데이트한 상품 반환
    }

    @DeleteMapping(DELETE_PATH)
    public ResponseEntity<?> delete(@RequestHeader("productId")String productId) {
        byte[] id = Base64.getDecoder().decode(productId);
        int result = productService.deleteProduct(id);
        if (result == 0) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok("Product deleted successfully.");
    }

    @GetMapping(LIST_PATH)
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return handleResponse(orders);
    }

    @PutMapping(DELIVER_PATH)
    public ResponseEntity<List<OrderDTO>> deliver() {
        List<OrderDTO> orders = orderService.updateOrdersStatus();
        return handleResponse(orders);
    }

    private <T> ResponseEntity<List<T>> handleResponse(List<T> list) {
        if (list == null || list.isEmpty()) {
            return ResponseEntity.noContent().build(); // 리스트가 비어 있으면 204 No Content
        }
        return ResponseEntity.ok(list); // 리스트가 있으면 200 OK
    }
}
