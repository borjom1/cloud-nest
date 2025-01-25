package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.FileApiExternal;
import com.cloud.nest.fm.inout.request.FileMetaIn;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.fm.model.FileFilter;
import com.cloud.nest.fm.service.FileService;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection;
import jakarta.annotation.security.RolesAllowed;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.fm.FileApiExternal.EXTERNAL_BASE;
import static com.cloud.nest.fm.FileApiExternal.URL_FILES;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection.DEFAULT_VALUE;
import static com.cloud.nest.platform.model.auth.UserRole.UserRoleCodes.USER;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpHeaders.RANGE;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@RestController
@RequestMapping(EXTERNAL_BASE + URL_FILES)
@RolesAllowed(USER)
@RequiredArgsConstructor
public class FileApiExternalController implements FileApiExternal {

    private final FileService fileService;

    @PostMapping(URL_UPLOAD)
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
            @PathVariable(PARAM_ID) @Min(1L) Long id,
            @Valid @RequestBody FileMetaIn in
    ) {
        fileService.updateFileMeta(session.userId(), id, in);
        return completedFuture(null);
    }

    @DeleteMapping
    @ResponseStatus(NO_CONTENT)
    @Override
    public CompletableFuture<Void> deleteFilesByIds(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,

            @RequestParam(PARAM_IDS)
            @Size(min = 1, max = 50)
            Set<@Min(1L) Long> fileIds
    ) {
        fileService.deleteFilesByIds(session.userId(), fileIds);
        return completedFuture(null);
    }

    @GetMapping
    @Override
    public CompletableFuture<List<FileOut>> getFiles(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @RequestParam(value = PARAM_FILENAME, required = false) String filename,
            @RequestParam(value = PARAM_EXT, required = false) String extension,
            @RequestParam(value = PARAM_MIN_FILE_SIZE, required = false) Long minFileSize,
            @RequestParam(value = PARAM_MAX_FILE_SIZE, required = false) Long maxFileSize,
            @RequestParam(PARAM_OFFSET) int offset,
            @RequestParam(PARAM_LIMIT) int limit
    ) {
        return completedFuture(fileService.getFilesByUserId(
                session.userId(),
                FileFilter.builder()
                        .filename(filename)
                        .ext(extension)
                        .minFileSize(minFileSize)
                        .maxFileSize(maxFileSize)
                        .build(),
                offset, limit
        ));
    }

    @GetMapping(PATH_ID + URL_DOWNLOAD)
    @Override
    public CompletableFuture<ResponseEntity<Resource>> downloadFileById(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) @Min(1L) Long id
    ) {
        return completedFuture(
                fileService.downloadUserFile(session.userId(), id).toResponseEntity()
        );
    }

    @GetMapping(PATH_ID + URL_DOWNLOAD + URL_RANGE)
    @Override
    public ResponseEntity<StreamingResponseBody> downloadFilePartByFileId(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) @Min(1L) Long id,
            @RequestHeader(value = RANGE, required = false, defaultValue = DEFAULT_VALUE) ContentRangeSelection rangeSelection
    ) {
        return fileService.downloadFilePart(session.userId(), id, rangeSelection).toResponseEntity();
    }

}
