package com.ctdecomerce.store.retailers.controller;


import com.ctdecomerce.store.retailers.dto.UserCheckDto;
import com.ctdecomerce.store.retailers.dto.UserIdRequest;
import com.ctdecomerce.store.retailers.service.RetailersService;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ctdecomerce.store.retailers.dto.IsRetailer;

@Setter
@RestController("RetailersDashboardController")
@RequestMapping("/retailerdashboard")
public class RetailersDashboardController {
    private RetailersService retailersService;

    public RetailersDashboardController(RetailersService retailersService) {
        this.retailersService = retailersService;
    }

    @PostMapping()
    public ResponseEntity<IsRetailer> checkIsRetailer(@RequestBody UserIdRequest userIdRequest) {
        return new ResponseEntity<>(retailersService.checkIfRetailer(userIdRequest), HttpStatus.OK);
    }

}
