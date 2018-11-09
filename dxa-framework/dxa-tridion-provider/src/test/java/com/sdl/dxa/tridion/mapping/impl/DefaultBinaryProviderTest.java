package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.StaticContentItem;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.File;
import java.nio.file.Path;
import java.util.regex.Matcher;

import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.doCallRealMethod;
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
    private final String[] files = {"ballon-burner_tcm5-297_w1024_h311_n.jpg",
            "company-news-placeholder_tcm5-286_w1024_n.png",
            "duplicate_tcm5-286_w1024_n.png",
            "invalid_name.png",
            "wall_tcm5-308.jpg"};

    @Mock
    private ContentProvider contentProvider;

    @Spy
    @InjectMocks
    private DefaultBinaryProvider provider;

    @Before
    public void setUp(){
        doReturn(PATH_TO_FILES).when(provider).getBasePath();
    }

    @Test
    public void getPathToBinaryFiles(){
        Path result = provider.getPathToBinaryFiles(PUB_ID);

        assertEquals(PATH_TO_FILES + File.separator + "BinaryData" + File.separator + PUB_ID + File.separator + "media", result.toString());
    }

    @Test
    public void processBinaryFileNoFiles() throws Exception {
        assertNull(provider.processBinaryFile(contentProvider, BINARY_ID, PUB_ID, LOCALIZATION_PATH, null));
    }

    @Test
    public void processBinaryFile() throws Exception {
        StaticContentItem binaryContent = mock(StaticContentItem.class);
        when(contentProvider.getStaticContent(files[0], PUB_ID, LOCALIZATION_PATH)).thenReturn(binaryContent);

        StaticContentItem result = provider.processBinaryFile(contentProvider, BINARY_ID, PUB_ID, LOCALIZATION_PATH, files);

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
        doReturn(files).when(provider).getFiles(BINARY_ID, PUB_ID, pathToBinaries);
        StaticContentItem binaryContent = mock(StaticContentItem.class);
        doReturn(binaryContent).when(provider).processBinaryFile(contentProvider, BINARY_ID, PUB_ID, LOCALIZATION_PATH, files);

        StaticContentItem result = provider.getStaticContent(contentProvider, BINARY_ID, PUB_ID, LOCALIZATION_PATH);

        assertEquals(binaryContent, result);
    }

    @Test
    public void getFilenameFilter() {
        File file = mock(File.class);

        assertFalse(provider.getFilenameFilter(BINARY_ID, PUB_ID).accept(file, files[0]));
        assertTrue(provider.getFilenameFilter(BINARY_ID, PUB_ID).accept(file, files[1]));
        assertTrue(provider.getFilenameFilter(BINARY_ID, PUB_ID).accept(file, files[2]));
        assertFalse(provider.getFilenameFilter(BINARY_ID, PUB_ID).accept(file, files[3]));
        assertFalse(provider.getFilenameFilter(BINARY_ID, PUB_ID).accept(file, files[4]));
    }
}