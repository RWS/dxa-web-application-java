package com.sdl.dxa.tridion.mapping.impl;

import com.google.common.primitives.Ints;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.BinaryComponent;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariant;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariantConnection;
import com.sdl.web.pca.client.contentmodel.generated.BinaryVariantEdge;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.web.context.WebApplicationContext;

import javax.servlet.ServletContext;
import java.io.File;
import java.io.FilenameFilter;
import java.nio.file.Path;
import java.util.Collections;
import java.util.regex.Matcher;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mock;

@RunWith(MockitoJUnitRunner.class)
public class DefaultBinaryProviderTest {
    private static final String PATH_TO_FILES = "D:=projects=dxa=web-application-java=dxa-webapp=target=dxa-webapp"
            .replaceAll("=", Matcher.quoteReplacement(File.separator));

    private static final String LOCALIZATION_PATH = "/";
    private static final String PUB_ID = "5";
    private static final int BINARY_ID = 286;
    private final String[] files = {"ballon-burner_ish5-297_w1024_h311_n.jpg",
            "company-news-placeholder_ish5-286_w1024_n.png",
            "duplicate_ish5-286_w1024_n.png",
            "invalid_name.png",
            "wall_ish5-308.jpg",
            "wally_tcm18-118119.png"};

    @Mock
    private ContentProvider contentProvider;
    @Mock
    private ApiClient pcaClient;
    @Mock
    private ApiClientProvider pcaClientProvider;
    @Mock
    private WebApplicationContext webApplicationContext;

    private GraphQLBinaryProvider provider;
    @Mock
    private BinaryComponent binaryComponentForSites;
    @Mock
    private BinaryComponent binaryComponentForDocs;

    @Before
    public void setUp(){
        provider = spy(new GraphQLBinaryProvider(pcaClientProvider, webApplicationContext));
        doReturn(PATH_TO_FILES).when(provider).getBasePath();
        when(pcaClientProvider.getClient()).thenReturn(pcaClient);
        when(pcaClient.getBinaryComponent(ContentNamespace.Sites, Ints.tryParse(PUB_ID), BINARY_ID, null, null)).thenReturn(binaryComponentForSites);
        when(pcaClient.getBinaryComponent(ContentNamespace.Docs, Ints.tryParse(PUB_ID), BINARY_ID, null, null)).thenReturn(binaryComponentForDocs);
    }

    @Test
    public void getPathToBinaryFiles(){
        Path result = provider.getPathToBinaryFiles(PUB_ID);

        assertEquals(PATH_TO_FILES + File.separator + "BinaryData" + File.separator + PUB_ID + File.separator + "media", result.toString());
    }

    @Test(expected = DxaItemNotFoundException.class)
    public void processBinaryFileNoFiles() throws Exception {
        when(pcaClient.getBinaryComponent(com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites, Ints.tryParse(PUB_ID), BINARY_ID, null, null)).thenReturn(null);

        assertNull(provider.processBinaryFile(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH, null));

        verify(provider).downloadBinary(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH);
    }

    @Test
    public void processBinaryFileDownloadBinaryComponentWithoutVariants() throws Exception {
        assertNull(provider.processBinaryFile(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH, null));

        verify(provider).downloadBinary(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH);
    }

    @Test
    public void downloadBinary() throws Exception {
        BinaryVariantConnection variants = mock(BinaryVariantConnection.class);
        when(binaryComponentForDocs.getVariants()).thenReturn(variants);
        BinaryVariantEdge edge = mock(BinaryVariantEdge.class);
        when(variants.getEdges()).thenReturn(Collections.singletonList(edge));
        BinaryVariant variant = mock(BinaryVariant.class);
        when(edge.getNode()).thenReturn(variant);
        when(variant.getDownloadUrl()).thenReturn("ballon-burner_ish5-297_w1024_h311_n.jpg");
        when(variant.getPath()).thenReturn("/binary/39137/6723");
        StaticContentItem expected = mock(StaticContentItem.class);
        when(contentProvider.getStaticContent("ish", variant.getPath(), PUB_ID, LOCALIZATION_PATH)).thenReturn(expected);

        StaticContentItem result = provider.downloadBinary(contentProvider, "ish", BINARY_ID, PUB_ID, LOCALIZATION_PATH);

        verify(contentProvider).getStaticContent("ish", variant.getPath(), PUB_ID, LOCALIZATION_PATH);
        assertSame(expected, result);
    }

    @Test(expected = ContentProviderException.class)
    public void downloadBinaryException1() throws Exception {
        BinaryVariantConnection variants = mock(BinaryVariantConnection.class);
        when(binaryComponentForSites.getVariants()).thenReturn(variants);
        BinaryVariantEdge edge = mock(BinaryVariantEdge.class);
        when(variants.getEdges()).thenReturn(Collections.singletonList(edge));
        BinaryVariant variant = mock(BinaryVariant.class);
        when(edge.getNode()).thenReturn(variant);
        when(variant.getDownloadUrl()).thenReturn("ballon-burner_tcm5-297_w1024_h311_n.jpg");
        when(variant.getPath()).thenReturn("/binary/39137/6723");
        when(contentProvider.getStaticContent("tcm", variant.getPath(), PUB_ID, LOCALIZATION_PATH)).thenThrow(new RuntimeException());

        provider.downloadBinary(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH);
    }

    @Test
    public void downloadBinaryException2() throws Exception {
        BinaryVariantConnection variants = mock(BinaryVariantConnection.class);
        when(binaryComponentForSites.getVariants()).thenReturn(variants);
        BinaryVariantEdge edge = mock(BinaryVariantEdge.class);
        when(variants.getEdges()).thenReturn(Collections.singletonList(edge));
        BinaryVariant variant = mock(BinaryVariant.class);
        when(edge.getNode()).thenReturn(variant);
        when(variant.getDownloadUrl()).thenReturn(null);

        assertNull(provider.downloadBinary(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH));
    }

    @Test
    public void downloadBinaryNoEdges() throws Exception {
        BinaryVariantConnection variants = mock(BinaryVariantConnection.class);
        when(binaryComponentForSites.getVariants()).thenReturn(variants);
        when(variants.getEdges()).thenReturn(null);

        assertNull(provider.downloadBinary(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH));
    }

    @Test
    public void processBinaryFile() throws Exception {
        StaticContentItem binaryContent = mock(StaticContentItem.class);
        when(contentProvider.getStaticContent("tcm", files[0], PUB_ID, LOCALIZATION_PATH)).thenReturn(binaryContent);

        StaticContentItem result = provider.processBinaryFile(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH, files);

        assertEquals(binaryContent, result);
    }

    @Test
    public void getBasePathWithLastSeparator() {
        doCallRealMethod().when(provider).getBasePath();
        String path = "abcd";
        doReturn(path + File.separator).when(provider).getAppRealPath();

        assertEquals(path, provider.getBasePath());
    }

    @Test
    public void getBasePath() {
        doCallRealMethod().when(provider).getBasePath();
        String path = "abcd";
        doReturn(path).when(provider).getAppRealPath();

        assertEquals(path, provider.getBasePath());
    }

    @Test
    public void getStaticContent() throws Exception {
        Path pathToBinaries = mock(Path.class);
        doReturn(pathToBinaries).when(provider).getPathToBinaryFiles(PUB_ID);
        doReturn(files).when(provider).getFiles("tcm", BINARY_ID, PUB_ID, pathToBinaries);
        StaticContentItem binaryContent = mock(StaticContentItem.class);
        doReturn(binaryContent).when(provider).processBinaryFile(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH, files);

        StaticContentItem result = provider.getStaticContent(contentProvider, "tcm", BINARY_ID, PUB_ID, LOCALIZATION_PATH);

        assertEquals(binaryContent, result);
    }

    @Test
    public void getFilenameFilter() {
        File file = mock(File.class);

        assertFalse(provider.getFilenameFilter("ish", BINARY_ID, PUB_ID).accept(file, files[0]));
        assertTrue(provider.getFilenameFilter("ish", BINARY_ID, PUB_ID).accept(file, files[1]));
        assertTrue(provider.getFilenameFilter("ish", BINARY_ID, PUB_ID).accept(file, files[2]));
        assertFalse(provider.getFilenameFilter("ish", BINARY_ID, PUB_ID).accept(file, files[3]));
        assertFalse(provider.getFilenameFilter("ish", BINARY_ID, PUB_ID).accept(file, files[4]));
        assertTrue(provider.getFilenameFilter("tcm", 118119, "18").accept(file, files[5]));
    }

    @Test
    public void getFiles() {
        File dirToBinaries = mock(File.class);
        Path pathToBinaries = mock(Path.class);
        when(pathToBinaries.toFile()).thenReturn(dirToBinaries);
        when(dirToBinaries.list(any(FilenameFilter.class))).thenReturn(files);

        assertArrayEquals(files, provider.getFiles("tcm", BINARY_ID, PUB_ID, pathToBinaries));
    }

    @Test
    public void getFilenameFilterForSites() {
        File dirToBinaries = mock(File.class);
        FilenameFilter filter1 = provider.getFilenameFilter("tcm", BINARY_ID, PUB_ID);
        FilenameFilter filter2 = provider.getFilenameFilter("tcm", 118119, "18");

        assertFalse(filter1.accept(dirToBinaries, files[0]));
        assertFalse(filter1.accept(dirToBinaries, files[1]));
        assertFalse(filter1.accept(dirToBinaries, files[2]));
        assertFalse(filter1.accept(dirToBinaries, files[3]));
        assertFalse(filter1.accept(dirToBinaries, files[4]));
        assertFalse(filter1.accept(dirToBinaries, files[5]));
        assertTrue(filter2.accept(dirToBinaries, files[5]));
    }

    @Test
    public void getFilenameFilterForDocs() {
        File dirToBinaries = mock(File.class);
        FilenameFilter filter1 = provider.getFilenameFilter("ish", BINARY_ID, PUB_ID);
        FilenameFilter filter2 = provider.getFilenameFilter("ish", 118119, "18");

        assertFalse(filter1.accept(dirToBinaries, files[0]));
        assertTrue(filter1.accept(dirToBinaries, files[1]));
        assertTrue(filter1.accept(dirToBinaries, files[2]));
        assertFalse(filter1.accept(dirToBinaries, files[3]));
        assertFalse(filter1.accept(dirToBinaries, files[4]));
        assertFalse(filter1.accept(dirToBinaries, files[5]));
        assertFalse(filter2.accept(dirToBinaries, files[5]));
    }

    @Test
    public void getAppRealPath() {
        ServletContext servletContext = mock(ServletContext.class);
        when(webApplicationContext.getServletContext()).thenReturn(servletContext);
        when(servletContext.getRealPath("/")).thenReturn(PATH_TO_FILES);

        assertEquals(PATH_TO_FILES, provider.getAppRealPath());
    }
}