package com.sdl.webapp.common.config;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class LocalizationTest {

    private static final int PUBLICATION_ID = 93;
    private static final String PATH = "/test";
    private static final String MEDIA_ROOT = "/media";

    private static final String FAVICON_PATH = "/favicon.ico";
    private static final String SYSTEM_ASSETS_PATH = "/system/assets";

    private Localization localization;

    @Before
    public void setUp() {
        Map<String, String> configuration = new HashMap<>();
        Map<String, String> resources = new HashMap<>();
        ListMultimap<String, String> includes = ArrayListMultimap.create();

        localization = new Localization(PUBLICATION_ID, PATH, MEDIA_ROOT, configuration, resources, includes);
    }

    @Test
    public void testFavIconIsStaticResourcePath() {
        assertTrue(localization.isStaticResourceUrl(PATH + FAVICON_PATH));
    }

    @Test
    public void testSystemAssetsIsStaticResourcePath() {
        assertTrue(localization.isStaticResourceUrl(PATH + SYSTEM_ASSETS_PATH + "/css/main.css"));
    }

    @Test
    public void testImageIsStaticResourcePath() {
        assertTrue(localization.isStaticResourceUrl(PATH + MEDIA_ROOT + "/test.png"));
    }

    @Test
    public void testOtherPathIsNotStaticResourcePath() {
        assertFalse(localization.isStaticResourceUrl("/example" + MEDIA_ROOT + "/test.png"));
    }

    @Test
    public void testNonMediaRootIsNotStaticResourcePath() {
        assertFalse(localization.isStaticResourceUrl(PATH + "/images/test.png"));
    }
}
