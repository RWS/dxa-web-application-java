package com.sdl.webapp.common.api.mapping.semantic.config;

import org.junit.Test;

import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class SemanticFieldTest {

    @Test
    public void shouldGenerateXPath() {
        //given
        SemanticField single = new SemanticField("name", "/Metadata", false, Collections.emptyMap());
        SemanticField metadata = new SemanticField("name", "/Metadata/Test/Test2", false, Collections.emptyMap());
        SemanticField content = new SemanticField("name", "/TSISchema/Test/Test2", false, Collections.emptyMap());
        SemanticField withContext = new SemanticField("name", "/TSISchema/Test/Test2", false, Collections.emptyMap());

        //when
        String fromSingle = single.getXPath(null);
        String fromMetadata = metadata.getXPath(null);
        String fromContent = content.getXPath("");
        String fromContext = withContext.getXPath("tcm:Content/custom:TSISchema/custom:Test[1]/custom:field");

        //then
        assertEquals("tcm:Metadata/custom:Metadata", fromSingle);
        assertEquals("tcm:Metadata/custom:Metadata/custom:Test/custom:Test2", fromMetadata);
        assertEquals("tcm:Content/custom:TSISchema/custom:Test/custom:Test2", fromContent);
        assertEquals("tcm:Content/custom:TSISchema/custom:Test[1]/custom:field/custom:Test2", fromContext);
    }

    @Test
    public void shouldUseFieldPath_ToStorePath() {
        //given
        FieldPath fieldPath = new FieldPath("/Metadata/Test/Test2");

        //when
        SemanticField metadata = new SemanticField("name", "/Metadata/Test/Test2", false, Collections.emptyMap());

        //then
        assertEquals(fieldPath, metadata.getPath());
    }
}