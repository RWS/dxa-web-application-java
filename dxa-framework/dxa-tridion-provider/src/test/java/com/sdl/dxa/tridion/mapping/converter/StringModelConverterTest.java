package com.sdl.dxa.tridion.mapping.converter;

import com.sdl.dxa.api.datamodel.model.BinaryContentData;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ExternalContentData;
import com.sdl.dxa.tridion.mapping.ModelBuilderPipeline;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.exceptions.DxaException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class StringModelConverterTest {
    private static final String LOCALIZATION_ID = "123";

    @Mock
    private LinkResolver linkResolver;
    @Mock
    private WebRequestContext webRequestContext;

    @InjectMocks
    private StringModelConverter converter = new StringModelConverter();

    @Before
    public void init() {
        Localization localization = mock(Localization.class);
        when(localization.getId()).thenReturn(LOCALIZATION_ID );
        when(webRequestContext.getLocalization()).thenReturn(localization);
    }

    @Test
    public void testSimpleConvert() throws DxaException {
        //given
        EntityModelData data = new EntityModelData(null, "red blue", "3", "4", new ContentModelData(), new BinaryContentData(), new ExternalContentData());
        data.setSchemaId(null);
        TypeInformation typeInformation = new TypeInformation(String.class, null);
        ModelBuilderPipeline pipeline = mock(ModelBuilderPipeline.class);

        //when
        Object result = converter.convert("testdata", typeInformation, null, pipeline, null);

        //then
        assertEquals("testdata", result);
    }

    @Test
    public void testLinkConvert() throws DxaException {
        //given
        EntityModelData data = new EntityModelData(null, "red blue", "3", "4", new ContentModelData(), new BinaryContentData(), new ExternalContentData());
        data.setSchemaId(null);
        TypeInformation typeInformation = new TypeInformation(Link.class, null);
        ModelBuilderPipeline pipeline = mock(ModelBuilderPipeline.class);

        String pageRequestContextId = "321";
        when(webRequestContext.getPageContextId()).thenReturn(pageRequestContextId);
        //when
        String toConvert = "tcm:1-123";
        Object result = converter.convert(toConvert, typeInformation, null, pipeline, null);

        //then
        assertTrue("Didn't get an instance of Link", result instanceof Link);
        verify(linkResolver).resolveLink(toConvert, LOCALIZATION_ID, pageRequestContextId);
    }

}