package com.sdl.webapp.common.api.formatters;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class JsonFormatterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddMediaType() {
        //given
        JsonFormatter jsonFormatter = new JsonFormatter(null, null);

        //when
        List<String> mediaTypes = (List<String>) ReflectionTestUtils.getField(jsonFormatter, "mediaTypes");

        //then
        assertTrue(mediaTypes.contains("application/json"));
    }

    @Test
    public void shouldReturnSameModel() throws Exception {
        //given
        Object expected = new Object();

        //when
        Object result = new JsonFormatter(null, null).formatData(expected);

        //then
        assertSame(expected, result);
    }

    @Test
    public void shouldGetSyndicationItemFromTeaser() throws Exception {
        Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            // When
            new JsonFormatter(null, null).getSyndicationItem(null);
        });
    }
}