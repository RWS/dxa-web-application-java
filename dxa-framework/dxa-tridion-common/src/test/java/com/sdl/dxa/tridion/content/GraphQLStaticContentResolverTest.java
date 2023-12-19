package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.ContextData;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.BinaryComponent;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariant;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariantConnection;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariantEdge;
import com.sdl.web.pca.client.contentmodel.generated.Publication;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.tridion.broker.StorageException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GraphQLStaticContentResolverTest {

    private static final String LOCALIZATION_ID = "42";
    private static final String DOWNLOAD_URL = "DOWNLOAD_URL";
    private static final String BINARY_DATA_STRING = "path_not_in_request";

    @Mock
    private WebApplicationContext webApplicationContext;

    @Mock
    private ApiClientProvider pcaClientProvider;

    @Mock
    private ApiClient apiClient;

    @Mock
    private BinaryContentDownloader binaryContentDownloader;

    @InjectMocks
    private GraphQLStaticContentResolver graphQLStaticContentResolver;

    private BinaryComponent binaryComponent;
    private BinaryVariant binaryVariant;

    @BeforeEach
    public void init() throws Exception {
        MockServletContext context = new MockServletContext();
        when(webApplicationContext.getServletContext()).thenReturn(context);

        when(pcaClientProvider.getClient()).thenReturn(apiClient);

        graphQLStaticContentResolver = new GraphQLStaticContentResolver(webApplicationContext, pcaClientProvider, binaryContentDownloader);

        binaryComponent = new BinaryComponent();
        BinaryVariantConnection binaryVariantConnection = new BinaryVariantConnection();
        List<BinaryVariantEdge> edges = new ArrayList<>();
        BinaryVariantEdge binaryVariantEdge = new BinaryVariantEdge();
        binaryVariant = new BinaryVariant();
        binaryVariant.setDownloadUrl(DOWNLOAD_URL);
        binaryVariant.setPath("/binary_path.png");
        binaryVariantEdge.setNode(binaryVariant);
        edges.add(binaryVariantEdge);
        binaryVariantConnection.setEdges(edges);
        binaryComponent.setVariants(binaryVariantConnection);

        when(pcaClientProvider.getClient().getBinaryComponent(eq(ContentNamespace.Sites),
                eq(42),
                anyString(),
                eq(""),
                any())).thenReturn(binaryComponent);

        binaryComponent.getVariants().getEdges().get(0).getNode().getDownloadUrl();

        Publication publication = new Publication();
        publication.setPublicationUrl("publication URL");

        lenient().when(pcaClientProvider.getClient().getPublication(eq(ContentNamespace.Sites),
                eq(Integer.parseInt(LOCALIZATION_ID)),
                eq(""),
                any(ContextData.class))).thenReturn(publication);
    }

    @Test
    public void shouldResolveLocalizationPath_IfItIsNotPassedInRequest() throws ContentProviderException, StorageException, IOException {
        //given
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/path_not_in_request", LOCALIZATION_ID).build();
        String binaryDataString = "path_not_in_request";
        when(binaryContentDownloader.downloadContent(any(File.class), eq(DOWNLOAD_URL))).thenReturn(binaryDataString.getBytes());

        //when
        StaticContentItem item = graphQLStaticContentResolver.getStaticContent(requestDto);

        //then
        assertEquals("path_not_in_request", IOUtils.toString(item.getContent(), "UTF-8"));
        assertFalse(item.isVersioned());
    }

    @Test
    public void shouldReturnRightContentType() throws IOException, ContentProviderException {
        //given
        when(binaryContentDownloader.downloadContent(any(File.class), eq(DOWNLOAD_URL))).thenReturn(BINARY_DATA_STRING.getBytes());
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/content_type", "42").build();
        binaryVariant.setType("content_type");

        //when
        StaticContentItem item = graphQLStaticContentResolver.getStaticContent(requestDto);

        //then
        assertEquals("content_type", item.getContentType());
    }

    @Test
    public void shouldResolveFile_WhenRequested_WithAllData() throws Exception {
        //given
        when(binaryContentDownloader.downloadContent(any(File.class), eq(DOWNLOAD_URL))).thenReturn("all_data".getBytes());
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/all_data", "42")
                .localizationPath("/publication").baseUrl("http://base").build();

        //when
        StaticContentItem item = graphQLStaticContentResolver.getStaticContent(requestDto);

        //then
        assertEquals("all_data", IOUtils.toString(item.getContent(), "UTF-8"));
        assertFalse(item.isVersioned());
        assertEquals("application/octet-stream", item.getContentType());
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/publication/all_data").exists());
    }

    @Test
    public void shouldNotAffectBinaryPath_IfLocalizationIsRoot() throws ContentProviderException, IOException {
        //given
        when(binaryContentDownloader.downloadContent(any(File.class), eq(DOWNLOAD_URL))).thenReturn(BINARY_DATA_STRING.getBytes());
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/loc_root", "42")
                .localizationPath("/").baseUrl("http://base").build();

        //when
        graphQLStaticContentResolver.getStaticContent(requestDto);

        //then
        assertTrue(new File(webApplicationContext.getServletContext().getRealPath("/") + "/BinaryData/42/loc_root").exists());
    }


    /*
        TODO:
        Check line 182. Why do we need the Publication URL in the path?.
        Should this not be:
        a) the publication and media path?
        b) nothing at all?

        This test is not valid otherwise.
     */
    @Test
    public void shouldRemoveVersionNumber_FromRequestedBinary() throws ContentProviderException {
        //given
        when(binaryContentDownloader.downloadContent(any(File.class), eq(DOWNLOAD_URL))).thenReturn(BINARY_DATA_STRING.getBytes());
        StaticContentRequestDto requestDto = StaticContentRequestDto.builder("/system/v1.2/version", "42").build();

        //when
        graphQLStaticContentResolver.getStaticContent(requestDto);

        //then
        assertTrue(


                new File(webApplicationContext.getServletContext().getRealPath("/") +
                        "/BinaryData/42/" + "publication URL/" +
                "system/version").exists());
    }
}