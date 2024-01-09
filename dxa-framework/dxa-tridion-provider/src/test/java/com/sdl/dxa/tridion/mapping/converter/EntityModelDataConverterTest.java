package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.BinaryContentData;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ExternalContentData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertNull;

public class EntityModelDataConverterTest {

    private EntityModelDataConverter converter = new EntityModelDataConverter();

    @Test
    public void noSchemaShouldntBeConverted() throws DxaException {
        //given
        EntityModelData data = new EntityModelData(null, "red blue", "3", "4", new ContentModelData(), new BinaryContentData(), new ExternalContentData());
        data.setSchemaId(null);
        TypeInformation typeInformation = new TypeInformation(EntityModel.class, null);
        ModelBuilderPipeline pipeline = mock(ModelBuilderPipeline.class);
        EntityModel entityModelMock = mock(EntityModel.class);
        lenient().when(pipeline.createEntityModel(any(), any())).thenReturn(entityModelMock);

        //when
        Object result = converter.convert(data, typeInformation, null, pipeline, null);

        //then
        assertNull("EntityModelData without schema should return null", result);
    }

    @Test
    public void withSchemaShouldBeConverted() throws DxaException {
        //given
        EntityModelData data = new EntityModelData("id", "red blue", "3", "4", new ContentModelData(), new BinaryContentData(), new ExternalContentData());
        data.setSchemaId("Schemaid");
        TypeInformation typeInformation = new TypeInformation(EntityModel.class, null);
        ModelBuilderPipeline pipeline = mock(ModelBuilderPipeline.class);
        EntityModel entityModelMock = mock(EntityModel.class);
        lenient().when(pipeline.createEntityModel(any(), any())).thenReturn(entityModelMock);

        //when
        Object result = converter.convert(data, typeInformation, null, pipeline, null);

        //then
        assertNotNull("EntityModelData with schema should not return null", result);
    }
}