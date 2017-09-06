package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RichTextDataResolverTest {

    private RichTextDataConverter converter = new RichTextDataConverter();

    @Test
    public void shouldJoinFragments() throws FieldConverterException {
        //given 
        RichTextData data = new RichTextData(Lists.newArrayList("fragment-1", "fragment-2"));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();

        //when
        Object result = converter.convert(data, typeInformation, null, null, null);

        //then
        assertEquals("fragment-1fragment-2", result);
    }
}