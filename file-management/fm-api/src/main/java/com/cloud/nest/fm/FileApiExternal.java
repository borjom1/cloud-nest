package com.cloud.nest.fm;

import com.cloud.nest.fm.inout.UploadedFileOut;
import com.cloud.nest.platform.infrastructure.auth.UserAuthSession;
import org.springframework.web.multipart.MultipartFile;

public interface FileApiExternal {

    String URL_FILES = "/external/v1/files";

    String PARAM_FILE = "file";

    UploadedFileOut uploadFile(UserAuthSession session, MultipartFile... files);
}
