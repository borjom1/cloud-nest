package com.cloud.nest.fm.controller.multipart;

import com.cloud.nest.fm.model.SingleFileUpload;
import jakarta.servlet.http.HttpServletRequest;

public interface MultipartFileHandler {
    SingleFileUpload getSingleFileUpload(HttpServletRequest request);
}
