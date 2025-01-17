package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.request.FileMetaIn;
import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import jakarta.annotation.Nullable;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface FileApiExternal {

    String URL_EXTERNAL = "/external";
    String URL_FILES = "/v1/files";
    String URL_UPLOAD = "/upload";
    String URL_DOWNLOAD = "/download";
    String URL_SHARES = "/shares";

    String PARAM_FILE = "file";
    String PARAM_ID = "id";
    String PARAM_IDS = "ids";
    String PARAM_FILENAME = "filename";
    String PARAM_EXT = "ext";
    String PARAM_MIN_FILE_SIZE = "minFileSize";
    String PARAM_MAX_FILE_SIZE = "maxFileSize";
    String PARAM_OFFSET = "offset";
    String PARAM_LIMIT = "limit";

    String PATH_ID = "/{" + PARAM_ID + "}";

    String FILENAME_ATTACHMENT = "attachment; filename=%s";

    long TEN_GB_IN_BYTES = 10_000_000_000L;

    CompletableFuture<UploadedFileOut> uploadFile(UserAuthSession session, MultipartFile... files);

    CompletableFuture<Void> updateFileMeta(UserAuthSession session, @Min(1L) Long id, @Valid FileMetaIn in);

    CompletableFuture<Void> deleteFilesByIds(
            UserAuthSession session,
            @Size(min = 1, max = 50) Set<@Min(1L) Long> fileIds
    );

    CompletableFuture<List<FileOut>> getFiles(
            @NotNull UserAuthSession session,
            @Nullable String filename,
            @Nullable String extension,
            @Nullable @Min(0L) @Max(TEN_GB_IN_BYTES) Long minFileSize,
            @Nullable @Min(0L) @Max(TEN_GB_IN_BYTES) Long maxFileSize,
            @Min(0) int offset,
            @Min(0) @Max(500) int limit
    );

    CompletableFuture<SharedFileOut> shareFile(UserAuthSession session, @Min(1L) Long id, @Valid SharedFileIn in);

    CompletableFuture<List<SharedFileOut>> getAllSharedFilesByFileId(UserAuthSession session, @Min(1L) Long id);

    CompletableFuture<SharedFileOut> deleteSharedFile(UserAuthSession session, UUID shareId);

    CompletableFuture<ResponseEntity<Resource>> downloadFileByShareId(
            UserAuthSession session,
            UUID shareId,
            @Valid SharedFileDownloadIn in
    );

    CompletableFuture<ResponseEntity<Resource>> downloadOwnFileById(UserAuthSession session, @Min(1L) Long id);

}
