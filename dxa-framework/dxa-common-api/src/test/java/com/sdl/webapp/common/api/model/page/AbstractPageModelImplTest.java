package com.sdl.webapp.common.api.model.page;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static com.sdl.webapp.common.api.model.TestEntity.entity;
import static com.sdl.webapp.common.api.model.TestEntity.feedItem;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AbstractPageModelImplTest {

    private static final String XPM_PAGE_SETTINGS_MARKUP = "<!-- Page Settings: {\"PageID\":\"%s\",\"PageModified\":\"%s\"," +
            "\"PageTemplateID\":\"%s\",\"PageTemplateModified\":\"%s\"} -->";

    private static final String XPM_PAGE_SCRIPT = "<script type=\"text/javascript\" language=\"javascript\" defer=\"defer\" " +
            "src=\"%s/WebUI/Editors/SiteEdit/Views/Bootstrap/Bootstrap.aspx?mode=js\" id=\"tridion.siteedit\"></script>";

    private static String getExpectedXpnSettingsMarkup(ImmutableMap<String, Object> xpmMetadata) {
        return String.format(XPM_PAGE_SETTINGS_MARKUP,
                "123", xpmMetadata.get("PageModified"),
                "234", xpmMetadata.get("PageTemplateModified"));
    }

    private static String getExpectedXpmScriptMerkup(String cmsUrl) {
        return String.format(XPM_PAGE_SCRIPT, cmsUrl);
    }

    private static ImmutableMap.Builder<String, Object> getXpmBuilder() {
        Date pageModified = new DateTime().minus(Days.days(2)).toDate();
        Date templateModified = new DateTime().minus(Days.days(3)).toDate();
        return ImmutableMap.<String, Object>builder()
                .put("PageID", "123")
                .put("PageModified", pageModified)
                .put("PageTemplateID", "234")
                .put("PageTemplateModified", templateModified);
    }

    @Test
    public void shouldReturnIfIfContainsRegion() throws Exception {
        //given
        AbstractPageModelImpl pageModel = new DefaultPageModel();
        RegionModelSet regions = new RegionModelSetImpl();
        regions.add(new RegionModelImpl("testname"));
        pageModel.setRegions(regions);

        //when
        boolean containsRegion = pageModel.containsRegion("testname");
        boolean notContainsRegion = pageModel.containsRegion("notexisting");

        //then
        assertTrue(containsRegion);
        assertFalse(notContainsRegion);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowNPEIfXpmMetadataIsNull() {
        //given
        AbstractPageModelImpl pageModel = new DefaultPageModel();

        //when
        pageModel.setXpmMetadata(null);

        //then
        //NPE
    }

    @Test
    public void shouldNeverHaveNullXpmMetadata() {
        //given
        AbstractPageModelImpl pageModel = new DefaultPageModel();

        //when

        //then
        assertNotNull(pageModel.getXpmMetadata());
    }

    @Test
    public void shouldReturnXpmMetadataIfAllSet() {
        //given
        AbstractPageModelImpl pageModel = new DefaultPageModel();
        ImmutableMap<String, Object> xpmMetadata = getXpmBuilder().build();
        pageModel.setXpmMetadata(xpmMetadata);

        String cmsUrl = "http://cms.url";
        Localization localization = mock(Localization.class);
        when(localization.getConfiguration(eq("core.cmsurl"))).thenReturn(cmsUrl);

        //when
        String xpmMarkup = pageModel.getXpmMarkup(localization);

        //then
        verify(localization).getConfiguration(eq("core.cmsurl"));
        assertEquals(getExpectedXpnSettingsMarkup(xpmMetadata) + getExpectedXpmScriptMerkup(cmsUrl), xpmMarkup);
    }

    @Test
    public void shouldReturnXpmMetadataIfAllSet2() {
        //given
        AbstractPageModelImpl pageModel = new DefaultPageModel();
        String cmsUrl = "http://cms.url";
        ImmutableMap<String, Object> xpmMetadata = getXpmBuilder().put("CmsUrl", cmsUrl + '/').build();
        pageModel.setXpmMetadata(xpmMetadata);

        Localization localization = mock(Localization.class);

        //when
        String xpmMarkup = pageModel.getXpmMarkup(localization);

        //then
        verify(localization, never()).getConfiguration(anyString());
        assertEquals(getExpectedXpnSettingsMarkup(xpmMetadata) + getExpectedXpmScriptMerkup(cmsUrl), xpmMarkup);
    }

    @Test
    public void shouldCopyPageModel() throws DxaException {
        //given
        DefaultPageModel pageModel = new DefaultPageModel();
        pageModel.setId("123");
        pageModel.setName("name");
        pageModel.setTitle("title");
        pageModel.setHtmlClasses("setHtmlClasses");
        pageModel.setMvcData(MvcDataCreator.creator().fromQualifiedName("qwe:asd:zxc").create());
        RegionModelSet regions = new RegionModelSetImpl();
        regions.add(new RegionModelImpl("region"));
        pageModel.setRegions(regions);
        pageModel.setMeta(ImmutableMap.of("meta1", "value1", "meta2", "value2"));
        pageModel.setXpmMetadata(getXpmBuilder().build());

        //when
        DefaultPageModel pageModel2 = new DefaultPageModel(pageModel);

        //then
        assertFalse(pageModel == pageModel2);
        assertEquals(pageModel, pageModel2);
    }

    @Test
    public void shouldAddExtensionData() {
        //given
        AbstractPageModelImpl pageModel = new AbstractPageModelImpl() {
        };

        //when
        pageModel.addExtensionData("key", "value");

        //then
        assertEquals("value", pageModel.getExtensionData().get("key"));
    }

    @Test
    public void shouldSetExtensionData() {
        //given
        AbstractPageModelImpl pageModel = new AbstractPageModelImpl() {
        };
        HashMap<String, Object> extensionData = new HashMap<String, Object>() {{
            put("key", "value");
        }};

        //when
        pageModel.setExtensionData(extensionData);

        //then
        assertEquals("value", pageModel.getExtensionData().get("key"));
    }

    @Test
    public void shouldCollectFeedItems() throws DxaException {
        //given
        FeedItem feedItem = feedItem("1");
        FeedItem feedItem2 = feedItem("2");

        DefaultPageModel pageModel = new DefaultPageModel();
        RegionModelSetImpl regions = new RegionModelSetImpl();
        RegionModelImpl regionModel = new RegionModelImpl("name");
        regionModel.addEntity(entity(feedItem));
        regions.add(regionModel);

        RegionModelImpl regionModel2 = new RegionModelImpl("name2");
        regionModel.addEntity(entity(feedItem2));
        regions.add(regionModel2);

        pageModel.setRegions(regions);

        //when
        List<FeedItem> feedItems = pageModel.extractFeedItems();

        //then
        assertThat(feedItems, IsIterableContainingInOrder.contains(feedItem, feedItem2));
    }

    @Test
    public void shouldRemoveTcmPartFromPageId() {
        //given 
        DefaultPageModel pageModel = new DefaultPageModel();
        pageModel.setId("tcm:1-2-3");

        //when
        String pureId = pageModel.getIdWithoutTcm();

        //then
        assertEquals("2", pureId);
    }
}