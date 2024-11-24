package com.cloud.nest.fm.unit;

import com.cloud.nest.fm.util.filter.GreaterOrEqual;
import com.cloud.nest.fm.util.filter.JooqQueryFilterTranslator;
import com.cloud.nest.fm.util.filter.LessOrEqual;
import com.cloud.nest.fm.util.filter.Like;
import lombok.Builder;
import lombok.With;
import org.jooq.Condition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class JooqQueryFilterTranslatorTest {

    private static final JooqQueryFilterTranslator translator = new JooqQueryFilterTranslator();

    @DisplayName("Test filter query translation")
    @Test
    void testFilterQueryTranslation() {
        final FileFilter filter = FileFilter.builder()
                .fileName("file")
                .ext("jpg")
                .minFileSize(50_000L)
                .maxFileSize(1_000_000L)
                .build();

        final Condition c = translator.toCondition(filter);

        assertNotNull(c);
        assertTrue(c.toString().contains("file_name like '%file%'"));
        assertTrue(c.toString().contains("and ext like '%jpg%'"));
        assertTrue(c.toString().contains("and size >= 50000"));
        assertTrue(c.toString().contains("and size <= 1000000"));
    }

    @DisplayName("Test filter query translation with null values")
    @Test
    void testFilterQueryTransactionWithNullValues() {
        // with file field
        FileFilter filter = FileFilter.builder()
                .fileName("file")
                .build();

        Condition c = translator.toCondition(filter);

        assertNotNull(c);
        assertEquals(c.toString(), "file_name like '%file%'");

        // with file and ext fields
        filter = filter.withExt("png");

        c = translator.toCondition(filter);

        assertNotNull(c);
        assertEquals(
                """
                        (
                          file_name like '%file%'
                          and ext like '%png%'
                        )""",
                c.toString()
        );

        // with minFileSize field
        filter = FileFilter.builder().minFileSize(30_000L).build();

        c = translator.toCondition(filter);

        assertNotNull(c);
        assertEquals("size >= 30000", c.toString());
    }

    @With
    @Builder
    private record FileFilter(
            @Like String fileName,
            @Like String ext,
            @GreaterOrEqual("size") Long minFileSize,
            @LessOrEqual("size") Long maxFileSize
    ) {
    }

}
