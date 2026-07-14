package com.ctdecomerce.store.discounts.controller;

import com.ctdecomerce.store.discounts.dto.CreateDiscount;
import com.ctdecomerce.store.discounts.dto.FindByProduct;
import com.ctdecomerce.store.discounts.dto.UserIdRequest;
import com.ctdecomerce.store.discounts.model.DiscountsModel;
import com.ctdecomerce.store.discounts.service.DiscountsService;
import com.ctdecomerce.store.product.model.ProductModel;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Setter
@AllArgsConstructor
@RestController("DiscountsController")
@RequestMapping("/discounts")
public class DiscountsController {
    private final DiscountsService discountsService;

    @PostMapping("/product/get")
    public ResponseEntity<DiscountsModel> findDiscountsForProduct(@RequestBody FindByProduct product) {
        return new ResponseEntity<>(discountsService.getDiscountsByProduct(product), HttpStatus.OK);
    }

    @PostMapping("/create")
    public ResponseEntity<DiscountsModel> createDiscount(@RequestBody CreateDiscount createDiscount) {
        return new ResponseEntity<>(discountsService.createDicount(createDiscount), HttpStatus.CREATED);
    }

    @PostMapping("/get")
    public ResponseEntity<List<DiscountsModel>> getDiscounts(@RequestBody UserIdRequest userIdRequest) {
        return new ResponseEntity<>(discountsService.getDiscounts(userIdRequest), HttpStatus.OK);
    }
}
