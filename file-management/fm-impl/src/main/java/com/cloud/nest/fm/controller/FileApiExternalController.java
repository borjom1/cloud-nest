package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.FileApiExternal;
import com.cloud.nest.fm.service.FileService;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import static com.cloud.nest.fm.FileApiExternal.URL_FILES;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(URL_FILES)
@RequiredArgsConstructor
public class FileApiExternalController implements FileApiExternal {

    private final FileService fileService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Override
    public Long uploadFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @RequestPart(PARAM_FILE) MultipartFile... files
    ) {
        if (files.length == 0) {
            throw new IllegalArgumentException("No file to upload");
        }
        if (files.length != 1) {
            throw new IllegalArgumentException("Only 1 file per request is allowed");
        }
        if (files[0].isEmpty()) {
            throw new IllegalArgumentException("Uploaded file is empty");
        }
        return fileService.uploadFile(session.userId(), files[0]);
    }

}
