package com.ctdecomerce.store.user.controller;

import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Setter
@AllArgsConstructor
@RestController("UserController")
@RequestMapping("/user")
public class UserController {
    private final UserService userService;

    @PostMapping("/create")
    public ResponseEntity<UserModel> createNewUser(@RequestBody UserModel user) {
        return new ResponseEntity<>(userService.createNewUser(user), HttpStatus.CREATED);
    }
}
