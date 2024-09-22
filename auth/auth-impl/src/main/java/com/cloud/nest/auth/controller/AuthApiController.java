package com.cloud.nest.auth.controller;

import com.cloud.nest.auth.AuthApi;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthApiController implements AuthApi {
}
