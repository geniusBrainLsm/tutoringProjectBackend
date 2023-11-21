package com.lsm.backend.config;

import com.lsm.backend.exception.ResourceNotFoundException;
import com.lsm.backend.model.User;
import com.lsm.backend.repository.UserRepository;
import com.lsm.backend.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.lsm.backend.security.CurrentUser;

@RestController
public class UserController {
    @Autowired
    private UserRepository userRepository;
    @GetMapping("/user/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}