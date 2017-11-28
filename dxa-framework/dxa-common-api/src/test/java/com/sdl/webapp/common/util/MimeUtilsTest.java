package com.sdl.webapp.common.util;

import org.junit.Test;

import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class MimeUtilsTest {

    @Test
    public void shouldRetrieveMimeTypeByFilename() {
        assertEquals("image/svg+xml", MimeUtils.getMimeType("image.svg"));
        assertEquals("image/svg+xml", MimeUtils.getMimeType("image.SVG"));
        assertEquals("application/x-gtar", MimeUtils.getMimeType("core.tar.gz"));
        assertEquals("application/x-gtar", MimeUtils.getMimeType("core.TAR.GZ"));
        assertNull(MimeUtils.getMimeType("non_existing.file"));
    }

    @Test
    public void shouldRetrieveMimeTypeByAbbreviation() {
        assertEquals("image/svg+xml", MimeUtils.getMimeType("svg"));
        assertEquals("image/svg+xml", MimeUtils.getMimeType("SVG"));
        assertEquals("application/x-gtar", MimeUtils.getMimeType("TAR.GZ"));

        assertNull(MimeUtils.getMimeType("not_existing_mime_type"));
    }
}