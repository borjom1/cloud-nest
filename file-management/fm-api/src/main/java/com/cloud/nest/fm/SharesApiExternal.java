package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.request.SharedFileDownloadIn;
import com.cloud.nest.fm.inout.request.SharedFileIn;
import com.cloud.nest.fm.inout.response.SharedFileOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import com.cloud.nest.platform.infrastructure.streaming.ContentRangeSelection;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface SharesApiExternal {

    String URL_SHARES = "/shares";

    String EXTERNAL_BASE = "/external/v1" + URL_SHARES;

    String URL_FILES = "/files";
    String URL_DOWNLOAD = "/download";
    String URL_RANGE = "/range";

    String PARAM_FILE_ID = "id";
    String PARAM_SHARE_ID = "shareId";

    String PATH_FILE_ID = "/{" + PARAM_FILE_ID + "}";
    String PATH_SHARE_ID = "/{" + PARAM_SHARE_ID + "}";

    CompletableFuture<SharedFileOut> shareFile(UserAuthSession session, @Min(1L) Long id, @Valid SharedFileIn in);

    CompletableFuture<List<SharedFileOut>> getAllSharedFilesByFileId(UserAuthSession session, @Min(1L) Long id);

    CompletableFuture<Void> deleteSharedFile(UserAuthSession session, UUID shareId);

    CompletableFuture<ResponseEntity<Resource>> downloadFileByShareId(
            UserAuthSession session,
            UUID shareId,
            @Valid SharedFileDownloadIn in
    );

    ResponseEntity<StreamingResponseBody> downloadFilePartByShareId(
            UserAuthSession session,
            UUID shareId,
            ContentRangeSelection rangeSelection,
            @Valid SharedFileDownloadIn in
    );

}
