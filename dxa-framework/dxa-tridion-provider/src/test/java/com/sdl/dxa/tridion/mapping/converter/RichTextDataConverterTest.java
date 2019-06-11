package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.BinaryContentData;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ExternalContentData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class RichTextDataConverterTest {

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

    @Test
    public void testEmbeddedEntity() throws DxaException {
        //given
        EntityModelData embeddedEntity = new EntityModelData(null, "red blue", "3", "4", new ContentModelData(), new BinaryContentData(), new ExternalContentData());
        RichTextData data = new RichTextData(Lists.newArrayList("fragment-1", "fragment-2", embeddedEntity));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();
        ModelBuilderPipeline pipeline = mock(ModelBuilderPipeline.class);
        EntityModel entityModelMock = mock(EntityModel.class);

        //when
        when(pipeline.createEntityModel(any(), any())).thenReturn(entityModelMock);
        Object result = converter.convert(data, typeInformation, null, pipeline, null);

        //then
        verify(entityModelMock, times(1)).setEmbedded(true);
    }
}