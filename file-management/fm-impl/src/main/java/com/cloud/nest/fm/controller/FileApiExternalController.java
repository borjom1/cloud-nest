package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.FileApiExternal;
import com.cloud.nest.fm.inout.FileMetaIn;
import com.cloud.nest.fm.inout.FileOut;
import com.cloud.nest.fm.inout.UploadedFileOut;
import com.cloud.nest.fm.service.FileService;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.fm.FileApiExternal.URL_FILES;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(URL_FILES)
@RequiredArgsConstructor
public class FileApiExternalController implements FileApiExternal {

    private final FileService fileService;

    @PostMapping
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<UploadedFileOut> uploadFile(
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
        return completedFuture(fileService.uploadFile(session.userId(), files[0]));
    }

    @PatchMapping(PATH_ID)
    @Override
    public CompletableFuture<Void> updateFileMeta(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) Long id,
            @Valid @RequestBody FileMetaIn in
    ) {
        fileService.updateFileMeta(session.userId(), id, in);
        return completedFuture(null);
    }

    @GetMapping
    @Override
    public CompletableFuture<List<FileOut>> getFiles(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @RequestParam(PARAM_OFFSET) int offset,
            @RequestParam(PARAM_LIMIT) int limit
    ) {
        return completedFuture(fileService.getFilesByUserId(session.userId(), offset, limit));
    }

}
