package com.sdl.dxa.tridion.content;

import com.sdl.dxa.common.dto.StaticContentRequestDto;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.content.StaticContentNotLoadedException;
import com.sdl.webapp.common.util.ImageUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;

public class GenericStaticContentResolverTest {
    @Mock
    private StaticContentItem staticContentItem;

    private GenericStaticContentResolver resolver = spy(new GenericStaticContentResolver() {
        @Override
        protected @NotNull StaticContentItem createStaticContentItem(StaticContentRequestDto requestDto, File file, int publicationId, ImageUtils.StaticContentPathInfo pathInfo, String urlPath) throws ContentProviderException {
            return staticContentItem;
        }

        @Override
        protected @NotNull StaticContentItem getStaticContentItemById(int binaryId, StaticContentRequestDto requestDto) throws ContentProviderException {
            return staticContentItem;
        }

        @Override
        protected String resolveLocalizationPath(StaticContentRequestDto requestDto) throws StaticContentNotLoadedException {
            return null;
        }
    });

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void getContentPathAny() {
        assertEquals("/group/version.json", resolver.getContentPath("/version.json", "/group"));
        assertEquals("/group/version.json", resolver.getContentPath("/group/version.json", "/group"));
    }

    @Test
    public void getContentPathEnglish() {
        assertEquals("/en/group/system/config/version.json", resolver.getContentPath("/group/system/config/version.json", "/en"));
        assertEquals("/en/system/config/version.json", resolver.getContentPath("/en/system/config/version.json", "/en"));
        assertEquals("/en/version.json", resolver.getContentPath("/version.json", "/en"));
    }

    @Test
    public void getContentPathWithVersionEnglish() {
        assertEquals("/system/config/version.json", resolver.getContentPath("/system/v12.1/config/version.json", ""));
        assertEquals("/system/dynamo/system/v12.1/config/version.json", resolver.getContentPath("/system/v12.1/dynamo/system/v12.1/config/version.json", ""));
    }

    @Test
    public void getContentPathVenezuela() {
        //see SRQ-12673
        assertEquals("/ve/version.json", resolver.getContentPath("/version.json", "/ve"));
    }

    @Test
    public void getPublicationPath() {
        String path = "C:\\Users\\anonym\\dxa-webapp\\BinaryData\\309\\group\\system\\config\\_all.json";
        doReturn(path).when(resolver).getRealPath();

        assertEquals(path + File.separator + "BinaryData" + File.separator + "309",
                resolver.getPublicationPath("309"));
    }
}
