package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.SharesApiExternal;
import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.fm.service.FileService;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.fm.SharesApiExternal.EXTERNAL_BASE;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection.DEFAULT_VALUE;
import static com.cloud.nest.platform.model.auth.UserRole.UserRoleCodes.USER;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static java.util.concurrent.CompletableFuture.runAsync;
import static org.springframework.http.HttpHeaders.RANGE;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequestMapping(EXTERNAL_BASE)
@RolesAllowed(USER)
@RequiredArgsConstructor
public class SharesApiExternalController implements SharesApiExternal {

    private final FileService fileService;

    @PostMapping(URL_FILES + PATH_FILE_ID)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<SharedFileOut> shareFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_FILE_ID) @Min(1L) Long id,
            @Valid @RequestBody SharedFileIn in
    ) {
        return completedFuture(fileService.shareFile(session.userId(), id, in));
    }

    @GetMapping(URL_FILES + PATH_FILE_ID)
    @Override
    public CompletableFuture<List<SharedFileOut>> getAllSharedFilesByFileId(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_FILE_ID) @Min(1L) Long id
    ) {
        return completedFuture(fileService.getAllSharedFilesByFileId(session.userId(), id));
    }

    @DeleteMapping(PATH_SHARE_ID)
    @Override
    public CompletableFuture<Void> deleteSharedFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_SHARE_ID) UUID shareId
    ) {
        return runAsync(() -> fileService.deleteSharedFile(session.userId(), shareId));
    }

    @GetMapping(PATH_SHARE_ID + URL_DOWNLOAD)
    @Override
    public CompletableFuture<ResponseEntity<Resource>> downloadFileByShareId(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_SHARE_ID) UUID shareId,
            @RequestBody SharedFileDownloadIn in
    ) {
        return completedFuture(
                fileService.downloadFileByShareId(session.userId(), shareId, in).toResponseEntity()
        );
    }

    @GetMapping(PATH_SHARE_ID + URL_DOWNLOAD + URL_RANGE)
    @Override
    public ResponseEntity<StreamingResponseBody> downloadFilePartByShareId(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_SHARE_ID) UUID shareId,
            @RequestHeader(value = RANGE, required = false, defaultValue = DEFAULT_VALUE) ContentRangeSelection rangeSelection,
            @RequestBody SharedFileDownloadIn in
    ) {
        return fileService.downloadFilePartByShareId(session.userId(), shareId, rangeSelection, in).toResponseEntity();
    }

}
