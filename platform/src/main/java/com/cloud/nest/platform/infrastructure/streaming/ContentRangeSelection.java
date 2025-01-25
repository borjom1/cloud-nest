package com.cloud.nest.platform.infrastructure.streaming;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
public class ContentRangeSelection {

    public static final String DEFAULT_VALUE = ContentRangeSelectionConverter.BYTES_PREFIX + "0-";

    private Long startByte;
    private Long endByte;

    public Long getContentLength() {
        if (endByte == null) {
            return startByte;
        }
        return endByte - startByte;
    }

    @Override
    public String toString() {
        return "%d-%d".formatted(startByte, endByte);
    }

}
