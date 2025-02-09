package com.cloud.nest.fm.controller.multipart;

import com.cloud.nest.fm.model.SingleFileUpload;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.apache.commons.fileupload2.core.FileItemInput;
import org.apache.commons.fileupload2.core.FileItemInputIterator;
import org.apache.commons.fileupload2.jakarta.JakartaServletFileUpload;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class StreamingMultipartFileHandler implements MultipartFileHandler {

    private static final String MISSING_FIELD_MSG = "'%s' field is missing";
    private static final String MULTIPART_SIZE_FIELD = "size";
    private static final String MULTIPART_CONTENT_FIELD = "content";
    public static final String INVALID_FIELD_VALUE_MSG = "'%s' field contains invalid value";

    private static final int MAX_LONG_VALUE_LENGTH = String.valueOf(Long.MAX_VALUE).length();
    private static final int END_OF_STREAM = -1;

    private final JakartaServletFileUpload<?, ?> servletFileUpload;

    @Override
    public SingleFileUpload getSingleFileUpload(HttpServletRequest request) {
        if (!JakartaServletFileUpload.isMultipartContent(request)) {
            throw new MultipartException("Current request is not multipart");
        }

        final var singleFileUpload = new SingleFileUpload();
        try {
            final FileItemInputIterator itemIterator = servletFileUpload.getItemIterator(request);
            while (singleFileUpload.isIncomplete() && itemIterator.hasNext()) {
                final FileItemInput item = itemIterator.next();

                if (singleFileUpload.getSize() == null) {
                    if (!item.getFieldName().equals(MULTIPART_SIZE_FIELD)) {
                        throw new MultipartException(MISSING_FIELD_MSG.formatted(MULTIPART_SIZE_FIELD));
                    }

                    if (item.getContentType() != null) {
                        final MediaType mediaType = MediaType.parseMediaType(item.getContentType());
                        if (!mediaType.equals(MediaType.TEXT_PLAIN)) {
                            throw new MultipartException("'%s' field has invalid media type".formatted(MULTIPART_SIZE_FIELD));
                        }
                    }

                    final long fileSize = readFileSize(item);
                    singleFileUpload.setSize(fileSize);
                    continue;
                }

                if (singleFileUpload.getFileInput() == null) {
                    if (!item.getFieldName().equals(MULTIPART_CONTENT_FIELD)) {
                        throw new MultipartException(MISSING_FIELD_MSG.formatted(MULTIPART_CONTENT_FIELD));
                    }
                    singleFileUpload.setFileInput(item);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (singleFileUpload.isIncomplete()) {
            throw new MultipartException("file upload is incomplete");
        }
        return singleFileUpload;
    }

    private static long readFileSize(FileItemInput item) throws IOException {
        int readBytes = 0;
        byte[] buffer = new byte[MAX_LONG_VALUE_LENGTH];
        try (InputStream inputStream = item.getInputStream()) {
            int read = inputStream.read(buffer, readBytes, buffer.length);

            while (read != END_OF_STREAM) {
                readBytes += read;
                if (readBytes > MAX_LONG_VALUE_LENGTH) {
                    throw new MultipartException(INVALID_FIELD_VALUE_MSG.formatted(MULTIPART_SIZE_FIELD));
                }
                read = inputStream.read(buffer);
            }

            final String fileSizeValue = new String(buffer, StandardCharsets.UTF_8).trim();
            return Long.parseLong(fileSizeValue);
        } catch (NumberFormatException e) {
            throw new MultipartException(INVALID_FIELD_VALUE_MSG.formatted(MULTIPART_SIZE_FIELD));
        }
    }

}
