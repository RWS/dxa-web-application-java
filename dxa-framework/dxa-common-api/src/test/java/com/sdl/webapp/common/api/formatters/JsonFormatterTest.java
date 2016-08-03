package com.sdl.webapp.common.api.formatters;

import org.junit.Test;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

public class JsonFormatterTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldAddMediaType() {
        //given
        JsonFormatter jsonFormatter = new JsonFormatter(null, null);

        //when
        List<String> mediaTypes = (List<String>) ReflectionTestUtils.getField(jsonFormatter, "_mediaTypes");

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

    @Test(expected = UnsupportedOperationException.class)
    public void shouldGetSyndicationItemFromTeaser() throws Exception {
        //when
        new JsonFormatter(null, null).getSyndicationItem(null);
    }

}