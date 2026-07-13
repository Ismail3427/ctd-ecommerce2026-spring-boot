package com.ctdecomerce.store.product.controller;

import com.ctdecomerce.store.dto.IdRequest;
import com.ctdecomerce.store.product.dto.*;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.service.ProductService;
import com.ctdecomerce.store.retailers.dto.RetailerIdRequest;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("ProductController")
@RequestMapping("/product")
public class ProductController {
    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @RateLimiter(name = "apiRateLimiter", fallbackMethod = "rateLimiterFallback")
    @PostMapping("/create-product")
    public ResponseEntity<ProductModel> createProduct(@RequestBody CreateProductDTO createProductDTO) {
        return new ResponseEntity<>(productService.createProduct(createProductDTO), HttpStatus.CREATED);
    }

    @RateLimiter(name = "apiRateLimiter", fallbackMethod = "rateLimiterFallback")
    @GetMapping("/all")
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }

    @RateLimiter(name = "apiRateLimiter", fallbackMethod = "rateLimiterFallback")
    @GetMapping("/get/{id}")
    public ResponseEntity<ProductModel> getProductById(@PathVariable String id) {
        IdRequest idReq = new IdRequest(id);
        return new ResponseEntity<>(productService.getProductById(idReq), HttpStatus.OK);
    }

    public ResponseEntity<String> rateLimiterFallback(RequestNotPermitted exception) {
        return ResponseEntity.status(429).body("TOO MANY REQUESTS");
    }

    @PostMapping("/change-name")
    public void setNewProductName(@RequestBody EditNameReqDto editNameReqDto) {
        productService.changeProductName(editNameReqDto);
    }

    @PostMapping("/change-description")
    public void setNewProductName(@RequestBody EditDescriptionReqDto editDescriptionReqDto) {
        productService.changeProductDescription(editDescriptionReqDto);
    }

    @PostMapping("/change-price")
    public void setNewProductName(@RequestBody EditPriceReqDto editPriceReqDto) {
        productService.changeProductPrice(editPriceReqDto);
    }





}
