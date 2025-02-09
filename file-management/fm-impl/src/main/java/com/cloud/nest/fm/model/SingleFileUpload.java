package com.cloud.nest.fm.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.fileupload2.core.FileItemInput;

@Getter
@Setter
public class SingleFileUpload {

    private Long size;
    private FileItemInput fileInput;

    public boolean isIncomplete() {
        return size == null || fileInput == null;
    }

}
