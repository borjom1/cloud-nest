package com.cloud.nest.fm.controller;

import com.cloud.nest.fm.FileApiExternal;
import com.cloud.nest.fm.inout.request.FileMetaIn;
import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.fm.model.DownloadedFile;
import com.cloud.nest.fm.model.FileFilter;
import com.cloud.nest.fm.service.FileService;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static com.cloud.nest.fm.FileApiExternal.URL_EXTERNAL;
import static com.cloud.nest.fm.FileApiExternal.URL_FILES;
import static com.cloud.nest.platform.infrastructure.request.RequestUtils.USER_SESSION_HEADER;
import static java.util.concurrent.CompletableFuture.completedFuture;
import static org.springframework.http.HttpStatus.*;

@RestController
@RequestMapping(URL_EXTERNAL + URL_FILES)
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

    @DeleteMapping(PATH_ID)
    @ResponseStatus(ACCEPTED)
    @Override
    public CompletableFuture<Void> deleteFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) @Min(1L) Long id
    ) {
        fileService.deleteFile(session.userId(), id);
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

    @PostMapping(PATH_ID + URL_SHARES)
    @ResponseStatus(CREATED)
    @Override
    public CompletableFuture<SharedFileOut> shareFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) @Min(1L) Long id,
            @Valid @RequestBody SharedFileIn in
    ) {
        return completedFuture(fileService.shareFile(session.userId(), id, in));
    }

    @GetMapping(PATH_ID + URL_SHARES)
    @Override
    public CompletableFuture<List<SharedFileOut>> getAllSharedFilesByFileId(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) @Min(1L) Long id
    ) {
        return completedFuture(fileService.getAllSharedFilesByFileId(session.userId(), id));
    }

    @DeleteMapping(URL_SHARES + PATH_ID)
    @ResponseStatus(NO_CONTENT)
    @Override
    public CompletableFuture<SharedFileOut> deleteSharedFile(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) UUID shareId
    ) {
        fileService.deleteSharedFile(session.userId(), shareId);
        return completedFuture(null);
    }

    @GetMapping(URL_SHARES + PATH_ID + URL_DOWNLOAD)
    @Override
    public CompletableFuture<ResponseEntity<Resource>> downloadFileByShareId(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) UUID shareId,
            @RequestBody SharedFileDownloadIn in
    ) {
        return completedFuture(
                downloadedFileToResponseEntity(
                        fileService.downloadFileByShareId(session.userId(), shareId, in)
                )
        );
    }

    @GetMapping(PATH_ID + URL_DOWNLOAD)
    @Override
    public CompletableFuture<ResponseEntity<Resource>> downloadOwnFileById(
            @RequestHeader(USER_SESSION_HEADER) UserAuthSession session,
            @PathVariable(PARAM_ID) @Min(1L) Long id
    ) {
        return completedFuture(
                downloadedFileToResponseEntity(
                        fileService.downloadUserFile(session.userId(), id)
                )
        );
    }

    @NotNull
    private ResponseEntity<Resource> downloadedFileToResponseEntity(@NotNull DownloadedFile file) {
        return ResponseEntity.ok()
                .contentType(file.contentType())
                .contentLength(file.size())
                .header(HttpHeaders.CONTENT_DISPOSITION, FILENAME_ATTACHMENT.formatted(file.name()))
                .body(file.resource());
    }

}
