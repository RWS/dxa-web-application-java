package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.entity.Article;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.impl.WebRequestContextImpl;
import com.sdl.webapp.common.impl.mapping.SemanticMappingRegistryImpl;
import com.sdl.webapp.common.impl.markup.MarkupTest.MarkupTestConfig.TestEntity;
import com.sdl.webapp.common.markup.Markup;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

public class MarkupImplTest {

    public MarkupImplTest() throws NoSuchFieldException {
    }

    private Markup markup = (MarkupImpl) new MarkupTest.MarkupTestConfig().markup();

    @Test
    public void shouldReturnWebRequestContext() {
        //given
        WebRequestContextImpl webRequestContext =  new WebRequestContextImpl();
        MarkupImpl markup = new MarkupImpl(new SemanticMappingRegistryImpl(), webRequestContext);

        //when
        WebRequestContext webRequestContextGet = markup.getWebRequestContext();

        //then
        assertEquals(webRequestContext, webRequestContextGet);
    }

    @Test
    public void shouldReturnUrl() {
        //given

        //when
        String url = markup.url("/markupUrl");

        //then
        assertEquals("/test/markupUrl", url);
    }

    @Test
    public void shouldReturnFormattedMessage() {
        //given

        //when
        String message = markup.formatMessage("This is {0}", "DXA");

        //then
        assertEquals("This is DXA", message);
    }

    @Test
    public void shouldReturnHtmlBreaks() {
        //given

        //when
        String message = markup.replaceLineEndsWithHtmlBreaks("This is DXA. Love it!");

        //then
        assertEquals("This is DXA<br/>Love it!", message);
    }

    @Test
    public void shouldReturnFormatDate() {
        //given

        //when
        String dateFormat = markup.formatDate(new DateTime(2014, 12, 11, 15, 39, 8, 834));

        //then
        assertEquals("Thursday, December 11, 2014", dateFormat);
    }

    @Test
    public void shouldReturnFormatDateDiffToday() {
        //given

        //when
        String dateFormat = markup.formatDateDiff(DateTime.now());

        //then
        assertEquals("TODAY", dateFormat);
    }

    @Test
    public void shouldReturnFormatDateDiffYesterday() {
        //given

        //when
        String dateFormat = markup.formatDateDiff(DateTime.now().minusDays(1));

        //then
        assertEquals("YESTERDAY", dateFormat);
    }

    @Test
    public void shouldReturnFormatDateDiffXDaysAgo() {
        for (int i = 2; i <= 7; i++) {
            //given

            //when
            String dateFormat = markup.formatDateDiff(DateTime.now().minusDays(i));

            //then
            assertEquals(i + " DAYS AGO", dateFormat);
        }
    }

    @Test
    public void shouldReturnFormatDateDiffLongerAgo() {
        for (int i = 8; i < 400; i++) {
            //given
            final DateTime dateTime = DateTime.now().minusDays(i);
            final String expected = DateTimeFormat.forPattern("d MMM yyyy").withLocale(Locale.US).print(dateTime);

            //when
            String dateFormat = markup.formatDateDiff(dateTime);

            //then
            assertEquals(expected, dateFormat);
        }
    }

    @Test
    public void shouldReturnResource() {
            //given

            //when
            String resource = markup.resource("core.todayText");

            //then
            assertEquals("TODAY", resource);
    }

    @Test
    public void shouldReturnVersionedContent() {
        //given

        //when
        String content = markup.versionedContent("/example");

        //then
        assertEquals("/test/xyz/system/v0.5/example", content);
    }

    @Test
    public void shouldReturnRegion() throws DxaException {
        //given
        RegionModelImpl region = new RegionModelImpl("TestRegion");

        //when
        String regionReturn = markup.region(region);

        //then
        assertEquals("typeof=\"Region\" resource=\"TestRegion\"", regionReturn);
    }

    @Test
    public void shouldReturnEntity() {
        //given
        TestEntity entity = new TestEntity();

        //when
        String entityReturn = markup.entity(entity);
        String entityReturn2 = markup.entity(new Article());

        //then
        assertEquals("prefix=\"s: http://schema.org/\" typeof=\"s:SchemaEnt\"", entityReturn);
        assertEquals("", entityReturn2);
    }

    @Test
    public void shouldReturnProperty() {
        //given
        TestEntity entity = new TestEntity();
        HashMap<String, String> map = new HashMap<String, String>() {{put("testField","xmp");}};
        entity.setXpmPropertyMetadata(map);

        //when
        String propertyReturn = markup.property(entity, "testField");
        String propertyReturn2 = markup.property(entity, "testField", 0);
        String propertyReturn3 = markup.property(entity, "testField2");

        //then
        assertEquals("property=\"s:TheField\" data-entity-property-xpath=\"xmp[1]\"", propertyReturn);
        assertEquals("property=\"s:TheField\" data-entity-property-xpath=\"xmp[1]\"", propertyReturn2);
        assertEquals("", propertyReturn3);
    }

    @Test
    public void shouldReturnSiteMapList() {
        //given
        SitemapItem sitemap = new SitemapItem();
        sitemap.setUrl("http://dxa.com/");
        sitemap.setTitle("DXA");

        SitemapItem sitemapChild = new SitemapItem();
        sitemapChild.setUrl("http://dxa.com/index");
        sitemapChild.setTitle("DXAChild");

        List<SitemapItem> siteItems = new ArrayList<>();
        siteItems.add(sitemapChild);

        sitemap.setItems(siteItems);

        //when
        String siteMapListReturn = markup.siteMapList(sitemap);

        //then
        assertEquals("<li><a href=\"http://dxa.com/\" title=\"DXA\">DXA</a><ul class=\"list-unstyled\"></ul></li>", siteMapListReturn);
    }
}