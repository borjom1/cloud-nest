package com.cloud.nest.fm.util;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.With;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang3.StringUtils;

@UtilityClass
public class FileUtils {

    public static final String EXT_SEPARATOR = ".";
    public static final int MAX_FILENAME_LENGTH = 64;
    public static final int MAX_EXTENSION_LENGTH = 8;

    @NotNull
    public static Filename2Ext getFilenameAndExt(@NotNull String filename) {
        final int separatorIdx = filename.lastIndexOf(EXT_SEPARATOR);
        return new Filename2Ext(
                filename.substring(0, separatorIdx),
                filename.substring(separatorIdx + 1)
        );
    }

    @NotNull
    public static Filename2Ext truncate(@NotNull Filename2Ext filename2Ext) {
        return filename2Ext
                .withFilename(StringUtils.truncate(filename2Ext.filename(), MAX_FILENAME_LENGTH))
                .withExt(StringUtils.truncate(filename2Ext.ext(), MAX_EXTENSION_LENGTH));
    }

    @With
    public record Filename2Ext(@NotNull String filename, @Nullable String ext) {
    }

}
