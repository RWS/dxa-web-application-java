package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.web.api.meta.WebComponentMetaFactoryImpl;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.tridion.broker.StorageException;
import com.tridion.content.BinaryFactory;
import com.tridion.data.BinaryData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import com.tridion.util.TCDURI;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(PowerMockRunner.class)
@PrepareForTest(CilStaticContentResolver.class)
public class CilStaticContentResolverTest {

    @Mock
    private PublicationMetaFactory publicationMetaFactory;

    @Mock
    private BinaryFactory binaryFactory;

    @Mock
    private DynamicMetaRetriever dynamicMetaRetriever;

    @Mock
    private WebApplicationContext webApplicationContext;

    @Mock
    private WebComponentMetaFactoryImpl webComponentMetaFactory;

    @Mock
    private PublicationMeta publicationMeta;

    @Mock
    private BinaryMeta binaryMeta;

    @Mock
    private BinaryData binaryData;

    @Mock
    private ComponentMeta componentMeta;

    private CilStaticContentResolver staticContentResolver;

    @Before
    public void init() throws Exception {
        PowerMockito.whenNew(DynamicMetaRetriever.class).withAnyArguments().thenReturn(dynamicMetaRetriever);
        PowerMockito.whenNew(BinaryFactory.class).withAnyArguments().thenReturn(binaryFactory);
        PowerMockito.whenNew(PublicationMetaFactory.class).withAnyArguments().thenReturn(publicationMetaFactory);
        PowerMockito.whenNew(WebComponentMetaFactoryImpl.class).withAnyArguments().thenReturn(webComponentMetaFactory);

        when(publicationMetaFactory.getMeta(anyString())).thenReturn(publicationMeta);
        when(publicationMeta.getPublicationUrl()).thenReturn("/");

        when(dynamicMetaRetriever.getBinaryMetaByURL(anyString())).thenReturn(binaryMeta);
        TCDURI tcduri = mock(TCDURI.class);
        when(binaryMeta.getURI()).thenReturn(tcduri);
        when(tcduri.getItemId()).thenReturn(123L);

        when(webComponentMetaFactory.getMeta(eq(123))).thenReturn(componentMeta);
        when(componentMeta.getLastPublicationDate()).thenReturn(new Date());

        when(binaryFactory.getBinary(eq(42), eq(123), anyString())).thenReturn(binaryData);
        when(binaryData.getBytes()).thenReturn("hello".getBytes());

        MockServletContext context = new MockServletContext();
        when(webApplicationContext.getServletContext()).thenReturn(context);

        staticContentResolver = new CilStaticContentResolver(webApplicationContext);
    }

    @Test
    public void shouldResolveLocalizationPath_IfItIsNotPassedInRequest() throws ContentProviderException, StorageException, IOException {
        //given 
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/path_not_in_request", "42").build();
        when(binaryData.getBytes()).thenReturn("path_not_in_request".getBytes());

        //when
        StaticContentItem item = staticContentResolver.getStaticContent(requestDto);

        //then
        verify(publicationMetaFactory).getMeta(eq("tcm:0-42-1"));
        assertEquals("path_not_in_request", IOUtils.toString(item.getContent(), "UTF-8"));
        assertFalse(item.isVersioned());
    }

    @Test
    public void shouldReturnRightContentType() throws IOException, ContentProviderException {
        //given 
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/content_type", "42").build();
        when(binaryData.getBytes()).thenReturn("content_type".getBytes());
        when(binaryMeta.getType()).thenReturn("content_type");

        //when
        StaticContentItem item = staticContentResolver.getStaticContent(requestDto);

        //then
        assertEquals("content_type", item.getContentType());
    }

    @Test
    public void shouldResolveFile_WhenRequested_WithAllData() throws Exception {
        //given
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/all_data", "42")
                .localizationPath("/publication").baseUrl("http://base").build();
        when(binaryData.getBytes()).thenReturn("all_data".getBytes());

        //when
        StaticContentItem item = staticContentResolver.getStaticContent(requestDto);

        //then
        assertEquals("all_data", IOUtils.toString(item.getContent(), "UTF-8"));
        assertFalse(item.isVersioned());
        assertEquals("application/octet-stream", item.getContentType());
        verify(publicationMetaFactory, never()).getMeta(anyString());
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/publication/all_data").exists());
    }

    @Test
    public void shouldNotAffectBinaryPath_IfLocalizationIsRoot() throws ContentProviderException, IOException {
        //given 
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/loc_root", "42")
                .localizationPath("/").baseUrl("http://base").build();
        when(binaryData.getBytes()).thenReturn("loc_root".getBytes());

        //when
        staticContentResolver.getStaticContent(requestDto);

        //then
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/loc_root").exists());
    }

    @Test
    public void shouldRemoveVersionNumber_FromRequestedBinary() throws ContentProviderException {
        //given 
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/system/v1.2/version", "42").build();

        //when
        staticContentResolver.getStaticContent(requestDto);

        //then
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/system/version").exists());
    }
}