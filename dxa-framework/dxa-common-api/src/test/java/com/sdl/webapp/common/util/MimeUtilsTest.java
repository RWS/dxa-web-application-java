package com.sdl.webapp.common.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

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

    @Test
    public void shouldLoadUserProperties() {
        //The "foo" mimetype is defined in src/test/resource/dxa.user.mimetypes.properties
        //MimeUtils should allow people to add new mimetype by creating this file in the classpath.

        assertEquals("bar", MimeUtils.getMimeType("foo"));
        assertEquals("bar", MimeUtils.getMimeType("foo"));
    }
}