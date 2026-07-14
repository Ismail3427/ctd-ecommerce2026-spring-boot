package com.ctdecomerce.store.retailers.controller;


import com.ctdecomerce.store.delivery.model.DeliveryModel;
import com.ctdecomerce.store.dto.AcctIdRequest;
import com.ctdecomerce.store.dto.LoginLinkRes;
import com.ctdecomerce.store.orders.model.OrdersModel;
import com.ctdecomerce.store.product.dto.EditNameReqDto;
import com.ctdecomerce.store.product.model.ProductModel;
import com.ctdecomerce.store.product.service.ProductService;
import com.ctdecomerce.store.retailers.dto.*;

import com.ctdecomerce.store.retailers.model.RetailersModel;
import com.ctdecomerce.store.retailers.service.RetailersService;
import com.stripe.exception.StripeException;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Setter
@RestController("RetailersDashboardController")
@RequestMapping("/retailer-dashboard")
public class RetailersDashboardController {
    private final ProductService productService;
    private RetailersService retailersService;

    public RetailersDashboardController(RetailersService retailersService, ProductService productService) {
        this.retailersService = retailersService;
        this.productService = productService;
    }

    @PostMapping()
    public ResponseEntity<IsRetailer> checkIsRetailer(@RequestBody UserIdRequest userIdRequest) {
        return new ResponseEntity<>(retailersService.checkIfRetailer(userIdRequest), HttpStatus.OK);
    }

    @PostMapping("/link")
    public ResponseEntity<LoginLinkRes> getLoginLink(@RequestBody AcctIdRequest acctIdRequest) throws StripeException {
        return new ResponseEntity<>(retailersService.generateLoginLink(acctIdRequest), HttpStatus.OK);
    }

    @PostMapping("/find-retailer")
    public ResponseEntity<RetailersModel> findRetailer(@RequestBody UserIdRequest userIdRequest) {
        return new ResponseEntity<>(retailersService.findRetailerFromUser(userIdRequest), HttpStatus.OK);
    }

    @PostMapping("/get-products")
    public List<ProductModel> getRetailersProducts(@RequestBody RetailerIdRequest retailerIdRequest) {
        return productService.getRetailersProducts(retailerIdRequest);
    }

    @PostMapping("/get-orders")
    public List<DeliveryModel> getRetailersOrders(@RequestBody RetailerIdRequest retailerIdRequest) {
        
        return retailersService.findRetailerOrders(retailerIdRequest);
    }

    @PostMapping("/get-product")
    public ProductModel getRetailerProduct(@RequestBody ProductIdRequest productIdRequest) {
        return productService.getRetailerProduct(productIdRequest);
    }
}
