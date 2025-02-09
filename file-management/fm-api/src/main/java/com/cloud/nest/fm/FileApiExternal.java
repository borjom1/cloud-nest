package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.request.FileMetaIn;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public interface FileApiExternal {

    String EXTERNAL_BASE = "/external";
    String URL_FILES = "/v1/files";
    String URL_UPLOAD = "/upload";
    String URL_DOWNLOAD = "/download";
    String URL_RANGE = "/range";

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

    CompletableFuture<UploadedFileOut> uploadFile(UserAuthSession session, HttpServletRequest request);

    CompletableFuture<Void> updateFileMeta(UserAuthSession session, @Min(1L) Long id, @Valid FileMetaIn in);

    CompletableFuture<Void> deleteFilesByIds(
            UserAuthSession session,
            @Size(min = 1, max = 50) Set<@Min(1L) Long> fileIds
    );

    CompletableFuture<List<FileOut>> getFiles(
            @NotNull UserAuthSession session,
            @Nullable String filename,
            @Nullable String extension,
            @Nullable @Min(0L) Long minFileSize,
            @Nullable @Min(0L) Long maxFileSize,
            @Min(0) int offset,
            @Min(0) @Max(500) int limit
    );

    CompletableFuture<ResponseEntity<Resource>> downloadFileById(UserAuthSession session, @Min(1L) Long id);

    ResponseEntity<StreamingResponseBody> downloadFilePartByFileId(
            UserAuthSession session,
            @Min(1L) Long id,
            ContentRangeSelection rangeSelection
    );

}
