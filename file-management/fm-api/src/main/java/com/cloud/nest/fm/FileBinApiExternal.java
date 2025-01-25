package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.response.FileBinOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface FileBinApiExternal {

    String EXTERNAL_BASE = "/external/v1/bin-files";

    String PARAM_ID = "id";
    String PARAM_OFFSET = "offset";
    String PARAM_LIMIT = "limit";

    String PATH_ID = "/{" + PARAM_ID + "}";

    CompletableFuture<Void> addFile(UserAuthSession session, Long fileId);

    CompletableFuture<Void> removeFile(UserAuthSession session, Long fileId);

    CompletableFuture<List<FileBinOut>> getFiles(UserAuthSession session, int offset, int limit);

}
