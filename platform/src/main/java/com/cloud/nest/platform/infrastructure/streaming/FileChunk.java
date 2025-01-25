package com.cloud.nest.platform.infrastructure.streaming;

import lombok.Builder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import static org.springframework.http.HttpHeaders.ACCEPT_RANGES;
import static org.springframework.http.HttpHeaders.CONTENT_RANGE;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.HttpStatus.PARTIAL_CONTENT;

@Builder
public record FileChunk(
        long chunkSize,
        long fileSize,
        MediaType contentType,
        ContentRangeSelection rangeSelection,
        StreamingResponseBody streamingBody
) {

    public ResponseEntity<StreamingResponseBody> toResponseEntity() {
        return ResponseEntity.status(chunkSize == fileSize ? OK : PARTIAL_CONTENT)
                .contentType(contentType)
                .contentLength(chunkSize)
                .header(ACCEPT_RANGES, "bytes")
                .header(CONTENT_RANGE, "bytes %d-%d/%d".formatted(
                        rangeSelection.getStartByte(),
                        rangeSelection.getEndByte() - 1,
                        fileSize()
                ))
                .body(streamingBody);
    }

}
