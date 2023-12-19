package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.tridion.broker.StorageException;
import com.tridion.content.BinaryFactory;
import com.tridion.data.BinaryData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.ComponentMetaFactory;
import com.tridion.meta.PublicationMeta;
import com.tridion.meta.PublicationMetaFactory;
import com.tridion.util.TCDURI;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CilStaticContentResolverTest {

    private MockedConstruction<PublicationMetaFactory> mockedConstructionPublicationMetaFactory;
    private MockedConstruction<BinaryFactory> mockedConstructionBinaryFactory;
    private MockedConstruction<DynamicMetaRetriever> mockedConstructionDynamicMetaRetriever;
    private MockedConstruction<ComponentMetaFactory> mockedConstructionWebComponentMetaFactory;

    @Mock
    private WebApplicationContext webApplicationContext;

    @Mock
    private PublicationMeta publicationMeta;

    @Mock
    private BinaryMeta binaryMeta;

    @Mock
    private BinaryData binaryData;

    @Mock
    private ComponentMeta componentMeta;

    @BeforeEach
    public void init() throws Exception {
        mockedConstructionDynamicMetaRetriever = Mockito.mockConstruction(DynamicMetaRetriever.class,
                (mock, context) -> lenient().when(mock.getBinaryMetaByURL(any())).thenReturn(binaryMeta));
        TCDURI tcduri = mock(TCDURI.class);
        lenient().when(binaryMeta.getURI()).thenReturn(tcduri);
        lenient().when(tcduri.getItemId()).thenReturn(123L);

        mockedConstructionBinaryFactory = Mockito.mockConstruction(BinaryFactory.class,
                (mock, context) -> lenient().when(mock.getBinary(anyInt(), anyInt(), any())).thenReturn(binaryData));
        lenient().when(binaryData.getBytes()).thenReturn("hello".getBytes());

        mockedConstructionPublicationMetaFactory = Mockito.mockConstruction(PublicationMetaFactory.class,
                (mock, context) -> lenient().when(mock.getMeta(any())).thenReturn(publicationMeta));
        lenient().when(publicationMeta.getPublicationUrl()).thenReturn("/");

        mockedConstructionWebComponentMetaFactory = Mockito.mockConstruction(ComponentMetaFactory.class,
                (mock, context) -> lenient().when(mock.getMeta(anyInt())).thenReturn(componentMeta));
        lenient().when(componentMeta.getLastPublicationDate()).thenReturn(new Date());

        MockServletContext context = new MockServletContext();
        lenient().when(webApplicationContext.getServletContext()).thenReturn(context);
    }

    @AfterEach
    public void afterEachTest() {
        mockedConstructionDynamicMetaRetriever.close();
        mockedConstructionBinaryFactory.close();
        mockedConstructionPublicationMetaFactory.close();
        mockedConstructionWebComponentMetaFactory.close();
    }

    @Test
    public void shouldResolveLocalizationPath_IfItIsNotPassedInRequest() throws ContentProviderException, StorageException, IOException {
        // Given
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/path_not_in_request", "42").build();
        lenient().when(binaryData.getBytes()).thenReturn("path_not_in_request".getBytes());

        // When
        PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();
        DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();
        BinaryFactory binaryFactory = new BinaryFactory();
        CilStaticContentResolver staticContentResolver = new CilStaticContentResolver(webApplicationContext,
                dynamicMetaRetriever, binaryFactory, publicationMetaFactory);

        StaticContentItem item = staticContentResolver.getStaticContent(requestDto);

        // Then
        verify(publicationMetaFactory).getMeta(eq("tcm:0-42-1"));
        assertEquals("path_not_in_request", IOUtils.toString(item.getContent(), "UTF-8"));
        assertFalse(item.isVersioned());
    }

    @Test
    public void shouldReturnRightContentType() throws IOException, ContentProviderException {
        // Given
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/content_type", "42").build();
        lenient().when(binaryData.getBytes()).thenReturn("content_type".getBytes());
        lenient().when(binaryMeta.getType()).thenReturn("content_type");

        // When
        PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();
        DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();
        BinaryFactory binaryFactory = new BinaryFactory();
        CilStaticContentResolver staticContentResolver = new CilStaticContentResolver(webApplicationContext,
                dynamicMetaRetriever, binaryFactory, publicationMetaFactory);
        StaticContentItem item = staticContentResolver.getStaticContent(requestDto);

        // Then
        assertEquals("content_type", item.getContentType());
    }

    @Test
    public void shouldResolveFile_WhenRequested_WithAllData() throws Exception {
        // Given
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/all_data", "42")
                .localizationPath("/publication").baseUrl("http://base").build();
        lenient().when(binaryData.getBytes()).thenReturn("all_data".getBytes());

        // When
        PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();
        DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();
        BinaryFactory binaryFactory = new BinaryFactory();
        CilStaticContentResolver staticContentResolver = new CilStaticContentResolver(webApplicationContext,
                dynamicMetaRetriever, binaryFactory, publicationMetaFactory);
        StaticContentItem item = staticContentResolver.getStaticContent(requestDto);

        // Then
        assertEquals("all_data", IOUtils.toString(item.getContent(), "UTF-8"));
        assertFalse(item.isVersioned());
        assertEquals("application/octet-stream", item.getContentType());
        verify(publicationMetaFactory, never()).getMeta(any());
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/publication/all_data").exists());
    }

    @Test
    public void shouldNotAffectBinaryPath_IfLocalizationIsRoot() throws ContentProviderException, IOException {
        //given 
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/loc_root", "42")
                .localizationPath("/").baseUrl("http://base").build();
        lenient().when(binaryData.getBytes()).thenReturn("loc_root".getBytes());

        //when
        PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();
        DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();
        BinaryFactory binaryFactory = new BinaryFactory();
        CilStaticContentResolver staticContentResolver = new CilStaticContentResolver(webApplicationContext,
                dynamicMetaRetriever, binaryFactory, publicationMetaFactory);
        staticContentResolver.getStaticContent(requestDto);

        //then
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/loc_root").exists());
    }

    @Test
    public void shouldRemoveVersionNumber_FromRequestedBinary() throws ContentProviderException {
        //given 
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/system/v1.2/version", "42").build();

        //when
        PublicationMetaFactory publicationMetaFactory = new PublicationMetaFactory();
        DynamicMetaRetriever dynamicMetaRetriever = new DynamicMetaRetriever();
        BinaryFactory binaryFactory = new BinaryFactory();
        CilStaticContentResolver staticContentResolver = new CilStaticContentResolver(webApplicationContext,
                dynamicMetaRetriever, binaryFactory, publicationMetaFactory);
        staticContentResolver.getStaticContent(requestDto);

        //then
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/")
                + "/BinaryData/42/system/version").exists());
    }
}