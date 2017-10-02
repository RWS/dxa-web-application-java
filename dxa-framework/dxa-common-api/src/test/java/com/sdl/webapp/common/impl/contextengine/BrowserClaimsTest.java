package com.sdl.webapp.common.impl.contextengine;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static com.sdl.webapp.common.impl.contextengine.Claims.getClaims;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BrowserClaimsTest {

    @Test
    public void shouldReturnBrowser() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();

        //when
        String aspectName = browserClaims.getAspectName();

        //then
        assertEquals("browser", aspectName);
    }

    @Test
    public void shouldReturnCssVersion() {
        //given
        String expected = "3.0";
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.cssVersion", expected));

        //when
        String cssVersion = browserClaims.getCssVersion();

        //then
        assertEquals(expected, cssVersion);
    }

    @Test
    public void shouldReturnDisplayColorDepth() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        int expected = 65536;
        browserClaims.setClaims(getClaims("browser.displayColorDepth", expected));

        //when
        int displayColorDepth = browserClaims.getDisplayColorDepth();

        //then
        assertEquals(expected, displayColorDepth);
    }

    @Test
    public void shouldReturnDisplayHeight() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        int expected = 728;
        browserClaims.setClaims(getClaims("browser.displayHeight", expected));

        //when
        int displayHeight = browserClaims.getDisplayHeight();

        //then
        assertEquals(expected, displayHeight);
    }

    @Test
    public void shouldReturnDisplayWidth() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        int expected = 1024;
        browserClaims.setClaims(getClaims("browser.displayWidth", expected));

        //when
        int displayWidth = browserClaims.getDisplayWidth();

        //then
        assertEquals(expected, displayWidth);
    }

    @Test
    public void shouldReturnJsVersion() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        String expected = "1.8";
        browserClaims.setClaims(getClaims("browser.jsVersion", expected));

        //when
        String jsVersion = browserClaims.getJsVersion();

        //then
        assertEquals(expected, jsVersion);
    }

    @Test
    public void shouldReturnModel() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        String expected = "modelClaim";
        browserClaims.setClaims(getClaims("browser.model", expected));

        //when
        String model = browserClaims.getModel();

        //then
        assertEquals(expected, model);
    }

    @Test
    public void shouldReturnVariant() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        String expected = "variantClaim";
        browserClaims.setClaims(getClaims("browser.variant", expected));

        //when
        String variant = browserClaims.getVariant();

        //then
        assertEquals(expected, variant);
    }

    @Test
    public void shouldReturnVendor() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        String expected = "vendorClaim";
        browserClaims.setClaims(getClaims("browser.vendor", expected));

        //when
        String vendor = browserClaims.getVendor();

        //then
        assertEquals(expected, vendor);
    }

    @Test
    public void shouldReturnVersion() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        String expected = "versionClaim";
        browserClaims.setClaims(getClaims("browser.version", expected));

        //when
        String version = browserClaims.getVersion();

        //then
        assertEquals(expected, version);
    }

    @Test
    public void shouldReturnPreferredHtmlContentType() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        String expected = "preferredHtmlContentTypeClaim";
        browserClaims.setClaims(getClaims("browser.preferredHtmlContentType", expected));

        //when
        String preferredHtmlContentType = browserClaims.getPreferredHtmlContentType();

        //then
        assertEquals(expected, preferredHtmlContentType);
    }

    @Test
    public void shouldReturnCookieSupport() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.cookieSupport", true));

        //when
        Boolean cookieSupport = browserClaims.getCookieSupport();

        //then
        assertTrue(cookieSupport);
    }

    @Test
    public void shouldReturnImageFormatSupport() {
        //given
        List<String> imageFormats = Lists.newArrayList("jpg" , "png");

        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.imageFormatSupport", imageFormats));

        //when
        String[] imageFormatSupport = browserClaims.getImageFormatSupport();

        //then
        assertArrayEquals(imageFormats.toArray(), imageFormatSupport);
    }

    @Test
    public void shouldReturnInputDevices() {
        //given
        List<String> devices = Lists.newArrayList("Webcam" , "Digital Camera");

        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.inputDevices", devices));

        //when
        String[] inputDevices = browserClaims.getInputDevices();

        //then
        assertArrayEquals(devices.toArray(), inputDevices);
    }

    @Test
    public void shouldReturnInputModeSupport() {
        //given
        List<String> inputModes = Lists.newArrayList("verbatim" , "full-width-latin");

        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.inputModeSupport", inputModes));

        //when
        String[] inputModeSupport = browserClaims.getInputModeSupport();

        //then
        assertArrayEquals(inputModes.toArray(), inputModeSupport);
    }

    @Test
    public void shouldReturnMarkupSupport() {
        //given
        List<String> markups = Lists.newArrayList("html" , "xml");

        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.markupSupport", markups));

        //when
        String[] markupSupport = browserClaims.getMarkupSupport();

        //then
        assertArrayEquals(markups.toArray(), markupSupport);
    }

    @Test
    public void shouldReturnScriptSupport() {
        //given
        List<String> scripts = Lists.newArrayList("html5" , "javascript");

        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.scriptSupport", scripts));

        //when
        String[] scriptSupport = browserClaims.getScriptSupport();

        //then
        assertArrayEquals(scripts.toArray(), scriptSupport);
    }

    @Test
    public void shouldReturnStylesheetSupport() {
        //given
        List<String> stylesheets = Lists.newArrayList("CSS3" , "XPath");

        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.stylesheetSupport", stylesheets));

        //when
        String[] stylesheetSupport = browserClaims.getStylesheetSupport();

        //then
        assertArrayEquals(stylesheets.toArray(), stylesheetSupport);
    }


}