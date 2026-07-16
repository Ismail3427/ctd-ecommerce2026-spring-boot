package com.ctdecomerce.store.user.service;

import com.ctdecomerce.store.user.model.UserModel;
import com.ctdecomerce.store.user.repository.UserRepo;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Service;

@Setter
@AllArgsConstructor
@Service
public class UserService {
    private final UserRepo userRepo;

    @Transactional
    public UserModel createNewUser(UserModel user) {
        UserModel checkUser = userRepo.findUserModelByUserId(user.getUserId());
        if (checkUser != null) {
            checkUser.setIpAddress(user.getIpAddress());
            checkUser.setLoginsCount(user.getLoginsCount());
            userRepo.save(checkUser);
            return checkUser;
        }
        return userRepo.save(user);
    }

}
