package com.cloud.nest.fm.persistence.s3;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface S3FileStorage {

    String FILENAME_META = "X-Amz-Meta-Filename";
    String FILE_EXT_META = "X-Amz-Meta-Ext";
    String AUTHOR_USER_ID = "X-Amz-Meta-Author_user_id";

    @NotBlank
    String uploadFile(
            @NotNull Long userId,
            @NotNull MultipartFile file,
            @NotNull Map<String, String> metadata
    );

}
