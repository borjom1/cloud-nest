package com.cloud.nest.platform.infrastructure.streaming;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import lombok.Builder;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;

@Log4j2
@Builder
public record ContentStreamingResponse(
        @Nonnull Supplier<InputStream> inputStreamProvider,
        @Nullable Consumer<Long> onReadFinished,
        @Nullable BiConsumer<Long, Exception> onError
) implements StreamingResponseBody {

    public static final int BUFFER_SIZE = 2048;

    @Override
    public void writeTo(@Nonnull OutputStream outputStream) {
        long readBytesCount = 0;
        IOException err = null;

        try (final var bis = new BufferedInputStream(inputStreamProvider.get())) {
            final byte[] buffer = new byte[BUFFER_SIZE];
            int readBytes;

            do {
                readBytes = bis.read(buffer);
                if (readBytes == -1) {
                    break;
                }
                outputStream.write(buffer, 0, readBytes);
                readBytesCount += readBytes;
            } while (true);
            outputStream.flush();

        } catch (IOException e) {
            err = e;
        }

        if (onReadFinished != null) {
            onReadFinished.accept(readBytesCount);
        }

        if (err != null && onError != null) {
            onError.accept(readBytesCount, err);
        }
    }

}
