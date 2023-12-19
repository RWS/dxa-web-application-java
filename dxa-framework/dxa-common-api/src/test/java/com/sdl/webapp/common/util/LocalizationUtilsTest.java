package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class LocalizationUtilsTest {

    @Test
    public void shouldRetrieveSchemaIdFromLocalization() {
        //given
        Localization localization = mock(Localization.class);
        lenient().when(localization.getConfiguration(eq("core.schemas.json"))).thenReturn("42");

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("json", localization);

        //then
        assertEquals(42, key);
    }

    @Test
    public void shouldRetrieveSchemaIdFromLocalizationForCustomKey() {
        //given
        Localization localization = mock(Localization.class);
        lenient().when(localization.getConfiguration(eq("custom.schemas.json"))).thenReturn("42");

        //when
        int key = LocalizationUtils.schemaIdFromSchemaKey("custom.json", localization);

        //then
        assertEquals(42, key);
    }

    @Test
    public void shouldReturnZeroIfFailedToParse() {
        //given
        Localization localization = mock(Localization.class);
        lenient().when(localization.getConfiguration(eq("core.schemas.json"))).thenReturn("asd");

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
    public void shouldReturnThePageIfFound() throws ContentProviderException {
        //given
        String path = "mypage.html";
        String publicationId = "1";
        String page = "page";

        LocalizationUtils.TryFindPage callback = mock(LocalizationUtils.TryFindPage.class);

        lenient().when(callback.tryFindPage(eq(path), eq(1))).thenReturn(page);

        Localization localization = mock(Localization.class);
        lenient().when(localization.getId()).thenReturn(publicationId);

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

        lenient().when(callback.tryFindPage(anyString(), eq(1))).thenReturn(null, page);

        Localization localization = mock(Localization.class);
        lenient().when(localization.getId()).thenReturn(publicationId);

        //when
        Object pageByPath = LocalizationUtils.findPageByPath(path, localization, callback);

        //then
        verify(localization).getId();
        verify(callback).tryFindPage(eq("mypage.html"), eq(1));
        verify(callback).tryFindPage(eq("mypage/index.html"), eq(1));
        assertEquals(page, pageByPath);
    }

    @Test
    public void shouldThrowExceptionIfNotFound() throws ContentProviderException {
        Assertions.assertThrows(PageNotFoundException.class, () -> {
            //given
            String path = "mypage.html";
            String publicationId = "1";

            LocalizationUtils.TryFindPage callback = mock(LocalizationUtils.TryFindPage.class);

            lenient().when(callback.tryFindPage(eq(path), eq(1))).thenReturn(null);

            Localization localization = mock(Localization.class);
            lenient().when(localization.getId()).thenReturn(publicationId);

            //when
            LocalizationUtils.findPageByPath(path, localization, callback);

            //then
            verify(localization).getId();
            verify(callback).tryFindPage(eq("mypage.html"), eq(1));
        });
    }
}