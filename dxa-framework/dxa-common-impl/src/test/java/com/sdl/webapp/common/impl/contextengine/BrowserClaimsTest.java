package com.sdl.webapp.common.impl.contextengine;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.util.List;

import static com.sdl.webapp.common.impl.contextengine.Claims.getClaims;
import static org.junit.Assert.*;

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
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.cssVersion", "3.0"));

        //when
        String cssVersion = browserClaims.getCssVersion();

        //then
        assertEquals("3.0", cssVersion);
    }

    @Test
    public void shouldReturnDisplayColorDepth() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.displayColorDepth", 65536));

        //when
        int displayColorDepth = browserClaims.getDisplayColorDepth();

        //then
        assertEquals(65536, displayColorDepth);
    }

    @Test
    public void shouldReturnDisplayHeight() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.displayHeight", 728));

        //when
        int displayHeight = browserClaims.getDisplayHeight();

        //then
        assertEquals(728, displayHeight);
    }

    @Test
    public void shouldReturnDisplayWidth() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.displayWidth", 1024));

        //when
        int displayWidth = browserClaims.getDisplayWidth();

        //then
        assertEquals(1024, displayWidth);
    }

    @Test
    public void shouldReturnJsVersion() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.jsVersion", "1.8"));

        //when
        String jsVersion = browserClaims.getJsVersion();

        //then
        assertEquals("1.8", jsVersion);
    }

    @Test
    public void shouldReturnModel() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.model", "modelClaim"));

        //when
        String model = browserClaims.getModel();

        //then
        assertEquals("modelClaim", model);
    }

    @Test
    public void shouldReturnVariant() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.variant", "variantClaim"));

        //when
        String variant = browserClaims.getVariant();

        //then
        assertEquals("variantClaim", variant);
    }

    @Test
    public void shouldReturnVendor() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.vendor", "vendorClaim"));

        //when
        String vendor = browserClaims.getVendor();

        //then
        assertEquals("vendorClaim", vendor);
    }

    @Test
    public void shouldReturnVersion() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.version", "versionClaim"));

        //when
        String version = browserClaims.getVersion();

        //then
        assertEquals("versionClaim", version);
    }

    @Test
    public void shouldReturnPreferredHtmlContentType() {
        //given
        BrowserClaims browserClaims = new BrowserClaims();
        browserClaims.setClaims(getClaims("browser.preferredHtmlContentType", "preferredHtmlContentTypeClaim"));

        //when
        String preferredHtmlContentType = browserClaims.getPreferredHtmlContentType();

        //then
        assertEquals("preferredHtmlContentTypeClaim", preferredHtmlContentType);
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