package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Test;

import static com.sdl.webapp.common.util.LocalizationUtils.DEFAULT_PAGE_EXTENSION;
import static com.sdl.webapp.common.util.LocalizationUtils.DEFAULT_PAGE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LocalizationUtilsTest {

    @Test
    public void shouldRetrieveSchemaIdFromLocalization() {
        //given
        Localization localization = mock(Localization.class);
        when(localization.getConfiguration(eq("core.schemas.json"))).thenReturn("42");

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("json", localization);

        //then
        assertEquals(42, key);
    }

    @Test
    public void shouldRetrieveSchemaIdFromLocalizationForCustomKey() {
        //given
        Localization localization = mock(Localization.class);
        when(localization.getConfiguration(eq("custom.schemas.json"))).thenReturn("42");

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("custom.json", localization);

        //then
        assertEquals(42, key);
    }

    @Test
    public void shouldReturnZeroIfFailedToParse() {
        //given
        Localization localization = mock(Localization.class);
        when(localization.getConfiguration(eq("core.schemas.json"))).thenReturn("asd");

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("json", localization);

        //then
        assertEquals(0, key);
    }

    @Test
    public void shouldReturnZeroIfKeyIsNull() {
        //given
        Localization localization = mock(Localization.class);

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("json", localization);

        //then
        assertEquals(0, key);
    }

    @Test
    public void shouldReturnZeroIfKeyIsInUnsupportedFormat() {
        //given
        Localization localization = mock(Localization.class);

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("my.dev.json", localization);

        //then
        assertEquals(0, key);
    }

    @Test
    public void shouldProcessDifferentKindsOfPaths() {
        //given
        String indexHtml = DEFAULT_PAGE_NAME + DEFAULT_PAGE_EXTENSION;

        //when
        String empty = LocalizationUtils.normalizePathToDefaults("");
        String slash = LocalizationUtils.normalizePathToDefaults("/");
        String test = LocalizationUtils.normalizePathToDefaults("test");
        String testSlash = LocalizationUtils.normalizePathToDefaults("/test/");
        String pageExt = LocalizationUtils.normalizePathToDefaults("page.ext");

        //then
        assertEquals(indexHtml, empty);
        assertEquals("/" + indexHtml, slash);
        assertEquals("test" + DEFAULT_PAGE_EXTENSION, test);
        assertEquals("/test/" + indexHtml, testSlash);
        assertEquals("page.ext", pageExt);
    }

    @Test
    public void shouldReturnThePageIfFound() throws ContentProviderException {
        //given
        String path = "mypage.html";
        String publicationId = "1";
        String page = "page";

        LocalizationUtils.TryFindPage callback = mock(LocalizationUtils.TryFindPage.class);

        when(callback.tryFindPage(eq(path), eq(1))).thenReturn(page);

        Localization localization = mock(Localization.class);
        when(localization.getId()).thenReturn(publicationId);

        //when
        Object pageByPath = LocalizationUtils.findPageByPath(path, localization, callback);

        //then
        verify(localization).getId();
        verify(callback).tryFindPage(eq(path), eq(1));
        assertEquals(page, pageByPath);
    }

    @Test
    public void shouldReturnThePageIfFoundAtSecondAttempt() throws ContentProviderException {
        //given
        String path = "mypage";
        String publicationId = "1";
        String page = "page";

        LocalizationUtils.TryFindPage callback = mock(LocalizationUtils.TryFindPage.class);

        when(callback.tryFindPage(anyString(), eq(1))).thenReturn(null, page);

        Localization localization = mock(Localization.class);
        when(localization.getId()).thenReturn(publicationId);

        //when
        Object pageByPath = LocalizationUtils.findPageByPath(path, localization, callback);

        //then
        verify(localization).getId();
        verify(callback).tryFindPage(eq("mypage.html"), eq(1));
        verify(callback).tryFindPage(eq("mypage/index.html"), eq(1));
        assertEquals(page, pageByPath);
    }

    @Test(expected = PageNotFoundException.class)
    public void shouldThrowExceptionIfNotFound() throws ContentProviderException {
        //given
        String path = "mypage.html";
        String publicationId = "1";

        LocalizationUtils.TryFindPage callback = mock(LocalizationUtils.TryFindPage.class);

        when(callback.tryFindPage(eq(path), eq(1))).thenReturn(null);

        Localization localization = mock(Localization.class);
        when(localization.getId()).thenReturn(publicationId);

        //when
        LocalizationUtils.findPageByPath(path, localization, callback);

        //then
        verify(localization).getId();
        verify(callback).tryFindPage(eq("mypage.html"), eq(1));
    }

    @SuppressWarnings("ConstantConditions")
    @Test
    public void shouldReplaceSequenceInPageTitle() {
        //when
        String s = LocalizationUtils.removeSequenceFromPageTitle("125 years of my company");
        String s1 = LocalizationUtils.removeSequenceFromPageTitle("125 years of my company");
        String s2 = LocalizationUtils.removeSequenceFromPageTitle("1256 years of my company");
        String s3 = LocalizationUtils.removeSequenceFromPageTitle("12 years of my company");
        String s4 = LocalizationUtils.removeSequenceFromPageTitle("012 12 years of my company");
        String s5 = LocalizationUtils.removeSequenceFromPageTitle(null);

        //then
        assertEquals("years of my company", s);
        assertEquals("years of my company", s1);
        assertEquals("1256 years of my company", s2);
        assertEquals("12 years of my company", s3);
        assertEquals("12 years of my company", s4);
        assertNull(s5);
    }
}