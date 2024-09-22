package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.FileApi;
import com.cloud.nest.um.UserApi;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class FileApiController implements FileApi {

    private final UserApi userApi;

    @GetMapping("/files")
    public String files() {
        return userApi.getUserString().join();
    }

}
