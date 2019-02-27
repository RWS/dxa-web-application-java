package com.sdl.dxa.tridion.graphql;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.MissingNode;
import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.common.dto.EntityRequestDto;
import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static com.sdl.dxa.common.util.PathUtils.normalizePathToDefaults;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLProviderTest {

    @Mock
    private ApiClient pcaClient;

    @Mock
    private ApiClientProvider apiClientProvider;

    private ObjectMapper mapper;
    private GraphQLProvider graphQLProvider;

    @Before
    public void setup() {
        when(apiClientProvider.getClient()).thenReturn(pcaClient);
        mapper = new DxaSpringInitialization().objectMapper();
        graphQLProvider = new GraphQLProvider(apiClientProvider, mapper);
    }

    @Test
    public void loadPageModel() throws Exception {
        PageRequestDto request = PageRequestDto.builder(5, "/index").build();
        JsonNode node = mapper.readTree(new ClassPathResource("pcaPageModel.json").getInputStream());
        when(pcaClient.getPageModelData(
                ContentNamespace.Sites,
                request.getPublicationId(),
                "/index.html",
                ContentType.MODEL,
                DataModelType.valueOf(request.getDataModelType().toString()),
                PageInclusion.valueOf(request.getIncludePages().toString()),
                ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                null))
                .thenReturn(node);
        PageModelData result = graphQLProvider._loadPage(PageModelData.class, request, ContentType.MODEL);

        assertEquals("/index", result.getUrlPath());
        assertEquals("Home", result.getTitle());
        assertEquals("640", result.getId());
    }

    @Test
    public void loadPageContentSecondTry() throws Exception {
        PageRequestDto request = PageRequestDto.builder(5, "/asdf").build();
        JsonNode node = mapper.readTree(new ClassPathResource("pcaPageModel.json").getInputStream());
        MissingNode missingNode = MissingNode.getInstance();
        when(pcaClient.getPageModelData(
                ContentNamespace.Sites,
                request.getPublicationId(),
                // path for the first try
                "/asdf.html",
                ContentType.MODEL,
                DataModelType.valueOf(request.getDataModelType().toString()),
                PageInclusion.valueOf(request.getIncludePages().toString()),
                ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                null))
                .thenThrow(IOException.class);

        when(pcaClient.getPageModelData(
                ContentNamespace.Sites,
                request.getPublicationId(),
                // path for second try
                "/asdf/index.html",
                ContentType.MODEL,
                DataModelType.valueOf(request.getDataModelType().toString()),
                PageInclusion.valueOf(request.getIncludePages().toString()),
                ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                null))
                .thenReturn(node);

        PageModelData result = graphQLProvider._loadPage(PageModelData.class, request, ContentType.MODEL);

        assertEquals("/index", result.getUrlPath());
        assertEquals("Home", result.getTitle());
        assertEquals("640", result.getId());
    }

    @Test
    public void loadPageContent() throws Exception {
        PageRequestDto request = PageRequestDto.builder(5, "/index").build();
        JsonNode node = mapper.readTree(new ClassPathResource("pcaPageModel.json").getInputStream());
        String expected = Resources.toString(Resources.getResource("pcaPageModelExpected.json"), Charsets.UTF_8);
        when(pcaClient.getPageModelData(
                ContentNamespace.Sites,
                request.getPublicationId(),
                normalizePathToDefaults("/index"),
                ContentType.RAW,
                DataModelType.valueOf(request.getDataModelType().toString()),
                PageInclusion.valueOf(request.getIncludePages().toString()),
                ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                null))
                .thenReturn(node);

        String result = graphQLProvider._loadPage(String.class, request, ContentType.RAW);

        assertEquals(expected, result);
    }

    @Test
    public void loadEntity() throws Exception {
        EntityRequestDto request = EntityRequestDto.builder(5, "333-444").build();
        JsonNode node = mapper.readTree(new ClassPathResource("pcaEntityModel.json").getInputStream());
        when(pcaClient.getEntityModelData(
                ContentNamespace.Sites,
                5,
                333,
                444,
                ContentType.MODEL,
                DataModelType.R2,
                DcpType.DEFAULT,
                ContentIncludeMode.INCLUDE_DATA_AND_RENDER,
                null
        )).thenReturn(node);

        EntityModelData result = graphQLProvider.getEntityModelData(request);

        assertEquals("1458-9195", result.getId());
        assertEquals("/about", result.getLinkUrl());
        assertEquals("tcm", result.getNamespace());
    }
}
