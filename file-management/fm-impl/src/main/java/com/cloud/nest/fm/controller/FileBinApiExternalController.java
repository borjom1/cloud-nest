package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.FileBinApiExternal;
import com.cloud.nest.fm.inout.response.FileBinOut;
import com.cloud.nest.fm.service.FileBinService;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import jakarta.annotation.security.RolesAllowed;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.fm.FileBinApiExternal.BASE_URL;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static com.cloud.nest.platform.model.auth.UserRole.UserRoleCodes.USER;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(BASE_URL)
@RolesAllowed(USER)
@RequiredArgsConstructor
public class FileBinApiExternalController implements FileBinApiExternal {

    private final FileBinService service;

    @PostMapping(PATH_ID)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<Void> addFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) Long fileId
    ) {
        service.addFileToBin(session.userId(), fileId);
        return completedFuture(null);
    }

    @DeleteMapping(PATH_ID)
    @ResponseStatus(NO_CONTENT)
    @Override
    public CompletableFuture<Void> removeFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) Long fileId
    ) {
        service.deleteFileFromBin(session.userId(), fileId);
        return completedFuture(null);
    }

    @GetMapping
    @Override
    public CompletableFuture<List<FileBinOut>> getFiles(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @RequestParam(PARAM_OFFSET) int offset,
            @RequestParam(PARAM_LIMIT) int limit
    ) {
        return completedFuture(service.getBinFiles(session.userId(), offset, limit));
    }

}
