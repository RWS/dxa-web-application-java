package com.sdl.dxa.common.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class PathUtilsTest {

    @Test
    public void shouldProcessDifferentKindsOfPaths() {
        //given
        String indexHtml = "index.html";

        //when
        String empty = PathUtils.normalizePathToDefaults("");
        String slash = PathUtils.normalizePathToDefaults("/");
        String test = PathUtils.normalizePathToDefaults("test");
        String testSlash = PathUtils.normalizePathToDefaults("/test/");
        String pageExt = PathUtils.normalizePathToDefaults("page.ext");

        //then
        assertEquals(indexHtml, empty);
        assertEquals("/" + indexHtml, slash);
        assertEquals("test.html", test);
        assertEquals("/test/" + indexHtml, testSlash);
        assertEquals("page.ext", pageExt);
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldReplaceSequenceInPageTitle() {
        //when
        String s = PathUtils.removeSequenceFromPageTitle("125 years of my company");
        String s1 = PathUtils.removeSequenceFromPageTitle("125 125 years of my company");
        String s2 = PathUtils.removeSequenceFromPageTitle("1256 years of my company");
        String s3 = PathUtils.removeSequenceFromPageTitle("12 years of my company");
        String s4 = PathUtils.removeSequenceFromPageTitle("012 12 years of my company");
        String s5 = PathUtils.removeSequenceFromPageTitle(null);

        //then
        assertEquals("years of my company", s);
        assertEquals("125 years of my company", s1);
        assertEquals("1256 years of my company", s2);
        assertEquals("12 years of my company", s3);
        assertEquals("12 years of my company", s4);
        assertNull(s5);
    }

    @Test
    public void shouldCheckIfPageTitleIsWithSequence() {
        assertTrue(PathUtils.isWithSequenceDigits("125 years of my company"));
        assertTrue(PathUtils.isWithSequenceDigits("125 125 years of my company"));
        assertFalse(PathUtils.isWithSequenceDigits("1256 years of my company"));
        assertFalse(PathUtils.isWithSequenceDigits("12 years of my company"));
        assertTrue(PathUtils.isWithSequenceDigits("012 12 years of my company"));
        assertFalse(PathUtils.isWithSequenceDigits(null));
    }

    @Test
    public void shouldStripDefaultExtension() {
        assertEquals("index", PathUtils.stripDefaultExtension("index.html"));
        assertEquals("index.ext", PathUtils.stripDefaultExtension("index.ext"));
        assertEquals("/path/to/index", PathUtils.stripDefaultExtension("/path/to/index.html"));
        assertEquals("/path/to/index", PathUtils.stripDefaultExtension("/path/to/index"));
        assertNull(PathUtils.stripDefaultExtension(null));
    }

    @Test
    public void shouldRemoveDoubleSlashFromPath() {
        String expected = "/path/to/index";
        assertEquals(expected, PathUtils.combinePath("/path", "to/index"));
        assertEquals(expected, PathUtils.combinePath("/path", "/to/index"));
        assertEquals(expected, PathUtils.combinePath("/path/", "to/index"));
        assertEquals(expected, PathUtils.combinePath("/path/", "/to/index"));
        assertEquals(expected, PathUtils.combinePath("/", "path/to/index"));
        assertEquals(expected, PathUtils.combinePath("", "/path/to/index"));
        assertEquals(expected, PathUtils.combinePath("/", "/path/to/index"));
        assertEquals("/", PathUtils.combinePath("/", ""));
        assertEquals("/", PathUtils.combinePath("//", ""));
        assertEquals("/", PathUtils.combinePath("", "/"));
        assertEquals("/", PathUtils.combinePath("", "//"));
        assertEquals("/", PathUtils.combinePath("", ""));
        assertEquals("/", PathUtils.combinePath(null, ""));
        assertEquals("/", PathUtils.combinePath("", null));
        assertNull(PathUtils.combinePath(null, null));
    }

    @Test
    public void shouldDefineWhetherThePathIsHome() {
        assertTrue(PathUtils.isHomePath("/", "/"));
        assertTrue(PathUtils.isHomePath("/", null));
        assertTrue(PathUtils.isHomePath("/childpub", "/childpub"));

        assertFalse(PathUtils.isHomePath("/nothome", "/"));
        assertFalse(PathUtils.isHomePath("/childpub/nothome", "/childpub"));
    }

    @Test
    public void shouldDefineIndexPath() {
        //when
        assertTrue(PathUtils.isIndexPath("/index.html"));
        assertTrue(PathUtils.isIndexPath("/page/index"));
        assertTrue(PathUtils.isIndexPath("/page/index.html"));
        assertTrue(PathUtils.isIndexPath("/page/long/path/index.html"));

        assertFalse(PathUtils.isIndexPath("/"));
        assertFalse(PathUtils.isIndexPath("/page/"));
        assertFalse(PathUtils.isIndexPath("/page"));
        assertFalse(PathUtils.isIndexPath("page"));
        assertFalse(PathUtils.isIndexPath(null));
    }

    @Test
    public void shouldStripIndexPath() {
        //when
        assertEquals("/", PathUtils.stripIndexPath("/index"));
        assertEquals("/", PathUtils.stripIndexPath("/index.html"));
        assertEquals("/page", PathUtils.stripIndexPath("/page/index"));
        assertEquals("/page", PathUtils.stripIndexPath("/page/index.html"));

        assertEquals("/page/page.html", PathUtils.stripIndexPath("/page/page.html"));
        assertEquals("/page/page", PathUtils.stripIndexPath("/page/page"));
        assertEquals("/page/page", PathUtils.stripIndexPath("/page/page/"));
        assertEquals("", PathUtils.stripIndexPath(""));
        assertEquals("/", PathUtils.stripIndexPath("/"));
        assertNull(PathUtils.stripIndexPath(null));
    }

    @Test
    public void shouldReplaceCurrentPathWithGiven() {
        //given
        String currentUrl = "http://sdl.com/my/path/index.html";

        //when
        String path = PathUtils.replaceContextPath(currentUrl, "/newPath.html");
        String path2 = PathUtils.replaceContextPath(currentUrl, "newPath.html");
        String path3 = PathUtils.replaceContextPath(currentUrl, "http://sdl.com/newPath.html");
        String path4 = PathUtils.replaceContextPath(currentUrl, "http://localhost:8080/newPath.html");

        //then
        assertEquals("http://sdl.com/newPath.html", path);
        assertEquals("http://sdl.com/newPath.html", path2);
        assertEquals("http://sdl.com/newPath.html", path3);
        assertEquals("http://sdl.com/newPath.html", path4);
    }

    @Test
    public void shouldReplaceCurrentRootPathWithGiven() {
        //when
        String path = PathUtils.replaceContextPath("http://sdl.com/", "/index.html");

        //then
        assertEquals("http://sdl.com/index.html", path);
    }

    @Test
    public void shouldResolveWhetherPathIsInRequestContext() {
        //given
        String path = "/page/about";
        String localizationPath = "/";

        //when, then
        assertTrue(PathUtils.isActiveContextPath(path, localizationPath, "/page"));
        assertTrue(PathUtils.isActiveContextPath(path, localizationPath, "/page/"));
        assertTrue(PathUtils.isActiveContextPath(path, localizationPath, "/page/about"));
        assertTrue(PathUtils.isActiveContextPath(path, localizationPath, "/page/about/"));

        assertFalse(PathUtils.isActiveContextPath(path, localizationPath, localizationPath));
        assertFalse(PathUtils.isActiveContextPath(path, localizationPath, "/page/about/path"));
        assertFalse(PathUtils.isActiveContextPath(path, localizationPath, "/other"));
        assertFalse(PathUtils.isActiveContextPath(path, localizationPath, null));
    }

    @Test
    public void shouldTreatHomeSpecially() {
        //given
        String path = "/";
        String localizationPath = "/";

        //when, then
        assertTrue(PathUtils.isActiveContextPath(path, localizationPath, "/"));
        assertFalse(PathUtils.isActiveContextPath(path, localizationPath, "/test"));
    }

    @Test
    public void shouldTreatHomeSpeciallyIfLocalizationIsNotRoot() {
        //given
        String path = "/test";
        String localizationPath = "/test";

        //when, then
        assertTrue(PathUtils.isActiveContextPath(path, localizationPath, "/test"));
        assertFalse(PathUtils.isActiveContextPath(path, localizationPath, "/"));
    }

    @Test
    public void shouldDetectDefaultExtension() {
        //when
        assertTrue(PathUtils.hasDefaultExtension("index.html"));
        assertTrue(PathUtils.hasDefaultExtension("page/index.html"));

        assertFalse(PathUtils.hasDefaultExtension("index.htm"));
        assertFalse(PathUtils.hasDefaultExtension("index.php"));
        assertFalse(PathUtils.hasDefaultExtension(".html"));
    }


    @Test
    public void shouldGetFileName_FromFullFilename() {
        //when
        assertEquals("filename", PathUtils.getFileName("/filename.html"));
        assertEquals("filename", PathUtils.getFileName("filename.html"));
        assertEquals("filename", PathUtils.getFileName("/test/filename.html"));
    }

    @Test
    public void shouldGetExtension_FromFullFilename() {
        //when
        assertEquals("html", PathUtils.getExtension("/filename.html"));
        assertEquals("html", PathUtils.getExtension("filename.html"));
        assertEquals("html", PathUtils.getExtension("/test/filename.html"));
    }

    @Test
    public void shouldSayIfPathHasExtensionPart() {
        //when
        boolean hasNoExtension = PathUtils.hasExtension("http://url.com/test");
        boolean hasExtension = PathUtils.hasExtension("http://url.com/test.html");

        //then
        assertTrue(hasExtension);
        assertFalse(hasNoExtension);
    }
}