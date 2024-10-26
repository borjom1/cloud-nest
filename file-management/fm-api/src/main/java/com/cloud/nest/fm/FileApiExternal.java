package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.FileMetaIn;
import com.cloud.nest.fm.inout.FileOut;
import com.cloud.nest.fm.inout.UploadedFileOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FileApiExternal {

    String URL_FILES = "/external/v1/files";

    String PARAM_FILE = "file";
    String PARAM_ID = "id";
    String PARAM_OFFSET = "offset";
    String PARAM_LIMIT = "limit";

    String PATH_ID = "/{" + PARAM_ID + "}";

    CompletableFuture<UploadedFileOut> uploadFile(UserAuthSession session, MultipartFile... files);

    CompletableFuture<Void> updateFileMeta(UserAuthSession session, Long id, FileMetaIn in);

    CompletableFuture<List<FileOut>> getFiles(UserAuthSession session, int offset, int limit);
}
