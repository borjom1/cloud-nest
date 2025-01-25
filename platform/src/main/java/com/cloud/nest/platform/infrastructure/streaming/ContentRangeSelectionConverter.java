package com.cloud.nest.platform.infrastructure.streaming;

import jakarta.annotation.Nonnull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

/**
 * A {@link Converter} implementation that converts a {@link String} representation of a byte range
 * into a {@link ContentRangeSelection} object. This converter is specifically designed to parse
 * HTTP Range headers of the format "bytes=start-end".
 *
 * <p>Usage example:
 * <pre> {@code
 * ContentRangeSelectionConverter converter = new ContentRangeSelectionConverter();
 * ContentRangeSelection selection = converter.convert("bytes=0-500");
 * } </pre>
 *
 * <p>The input string must adhere to the following rules:
 * <ul>
 *   <li>Must start with "bytes="</li>
 *   <li>Must include a valid range in the format "start-end" or "start-"</li>
 *   <li>Start must be a non-negative number</li>
 *   <li>End must be greater than start (if provided)</li>
 * </ul>
 *
 * <p>If the input does not conform to these rules, an {@link IllegalArgumentException} is thrown.
 *
 * <p>Example valid inputs:
 * <ul>
 *   <li>"bytes=0-500"</li>
 *   <li>"bytes=100-"</li>
 * </ul>
 *
 * <p>Example invalid inputs:
 * <ul>
 *   <li>"bytes=500-100"</li>
 *   <li>"bytes=-500"</li>
 *   <li>"bytes=abc-xyz"</li>
 *   <li>"range=0-500"</li>
 * </ul>
 *
 * @see ContentRangeSelection
 */
@Component
public class ContentRangeSelectionConverter implements Converter<String, ContentRangeSelection> {

    /**
     * Delimiter used to separate the start and end values of the range.
     */
    public static final String RANGE_DELIMITER = "-";
    private static final String INVALID_RANGE_FORMAT_ERROR = "Invalid range format: '%s'";
    public static final String BYTES_PREFIX = "bytes=";

    /**
     * Converts a {@link String} representation of a byte range into a {@link ContentRangeSelection}.
     *
     * @param source the string to convert, must not be null. Expected format: "bytes=start-end".
     * @return a {@link ContentRangeSelection} representing the parsed range.
     * @throws IllegalArgumentException if the input string is null, improperly formatted, or contains invalid range values.
     */
    @Override
    public ContentRangeSelection convert(@Nonnull String source) {
        final String[] rangeParts = resolveRangeParts(source.trim());
        try {
            long start = Long.parseLong(rangeParts[0]);
            if (start < 0) {
                throw new IllegalArgumentException("Range start value must be greater than or equal 0");
            }

            if (rangeParts.length > 1) {
                if (rangeParts.length > 2) {
                    throw new IllegalArgumentException(INVALID_RANGE_FORMAT_ERROR.formatted(source));
                }
                if (!rangeParts[1].isBlank()) {
                    long end = Long.parseLong(rangeParts[1]);
                    if (end < 1 || start >= end) {
                        throw new IllegalArgumentException("Invalid range values: '%s'".formatted(source));
                    }
                    return new ContentRangeSelection(start, end);
                }
            }
            return new ContentRangeSelection(start, null);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(INVALID_RANGE_FORMAT_ERROR.formatted(source));
        }
    }

    private String[] resolveRangeParts(String source) {
        source = source.trim();
        if (!source.startsWith(BYTES_PREFIX)) {
            throw new IllegalArgumentException(INVALID_RANGE_FORMAT_ERROR.formatted(source));
        }

        final String[] sourceParts = source.split("=");
        if (sourceParts.length != 2) {
            throw new IllegalArgumentException(INVALID_RANGE_FORMAT_ERROR.formatted(source));
        }

        final String[] rangeParts = sourceParts[1].trim().split(RANGE_DELIMITER);
        if (!sourceParts[1].contains(RANGE_DELIMITER) || rangeParts.length < 1) {
            throw new IllegalArgumentException(INVALID_RANGE_FORMAT_ERROR.formatted(source));
        }
        return rangeParts;
    }

}
