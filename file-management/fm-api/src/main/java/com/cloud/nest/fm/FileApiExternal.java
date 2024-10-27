package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.request.FileMetaIn;
import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.FileOut;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.fm.inout.response.UploadedFileOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
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
    String PARAM_OFFSET = "offset";
    String PARAM_LIMIT = "limit";

    String PATH_ID = "/{" + PARAM_ID + "}";

    String FILENAME_ATTACHMENT = "attachment; filename=%s";

    CompletableFuture<UploadedFileOut> uploadFile(UserAuthSession session, MultipartFile... files);

    CompletableFuture<Void> updateFileMeta(UserAuthSession session, Long id, FileMetaIn in);

    CompletableFuture<Void> deleteFile(UserAuthSession session, Long id);

    CompletableFuture<List<FileOut>> getFiles(UserAuthSession session, int offset, int limit);

    CompletableFuture<SharedFileOut> shareFile(UserAuthSession session, Long id, SharedFileIn in);

    CompletableFuture<List<SharedFileOut>> getAllSharedFilesByFileId(UserAuthSession session, Long id);

    CompletableFuture<SharedFileOut> deleteSharedFile(UserAuthSession session, UUID shareId);

    CompletableFuture<ResponseEntity<Resource>> downloadFileByShareId(
            UserAuthSession session,
            UUID shareId,
            SharedFileDownloadIn in
    );

    CompletableFuture<ResponseEntity<Resource>> downloadOwnFileById(UserAuthSession session, Long id);

}
