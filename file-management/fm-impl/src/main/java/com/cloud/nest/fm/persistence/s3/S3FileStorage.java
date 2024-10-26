package com.cloud.nest.fm.persistence.s3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface S3FileStorage {

    String FILENAME_META = "X-Amz-Init-Meta-Filename";
    String FILE_EXT_META = "X-Amz-Init-Meta-Ext";
    String AUTHOR_USER_ID = "X-Amz-Init-Meta-Author_User_Id";

    @NotBlank
    String uploadFile(
            @NotNull Long userId,
            @NotNull MultipartFile file,
            @NotNull Map<String, String> metadata
    );
}
