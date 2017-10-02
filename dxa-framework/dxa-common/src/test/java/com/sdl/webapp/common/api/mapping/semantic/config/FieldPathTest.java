package com.sdl.webapp.common.api.mapping.semantic.config;

import org.junit.Test;

import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class FieldPathTest {

    @Test
    public void shouldDetermineMetadataOrContent() {
        //given
        FieldPath metadata1 = new FieldPath("Metadata/Test");
        FieldPath metadata2 = new FieldPath("/Metadata/Test");
        FieldPath test = new FieldPath("/Test/Test2");
        FieldPath noTail = new FieldPath("Test2");

        //then
        assertEquals("Metadata", metadata1.getHead());
        assertEquals("Metadata", metadata2.getHead());
        assertEquals("Test", test.getHead());
        assertEquals("Test2", test.getTail().getHead());
        assertNull(test.getTail().getTail());
        assertTrue(test.hasTail());
        assertFalse(test.getTail().hasTail());

        assertEquals("Test2", noTail.getHead());
        assertNull(noTail.getTail());

        assertTrue(metadata1.isMetadata());
        assertTrue(metadata2.isMetadata());
        assertFalse(test.isMetadata());
    }
}