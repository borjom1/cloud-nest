package com.cloud.nest.um.controller;

import com.cloud.nest.um.service.UserService;
import com.cloud.nest.um.impl.UserApiStandalone;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(UserApiStandalone.URL_USERS)
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String get() {
        userService.save();
        return "user";
    }

}
