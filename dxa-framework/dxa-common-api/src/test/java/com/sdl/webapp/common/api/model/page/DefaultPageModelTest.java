package com.sdl.webapp.common.api.model.page;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class DefaultPageModelTest {

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
        DefaultPageModel pageModel = new DefaultPageModel();
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
        DefaultPageModel pageModel = new DefaultPageModel();

        //when
        pageModel.setXpmMetadata(null);

        //then
        //NPE
    }

    @Test
    public void shouldNeverHaveNullXpmMetadata() {
        //given
        DefaultPageModel pageModel = new DefaultPageModel();

        //when

        //then
        assertNotNull(pageModel.getXpmMetadata());
    }

    @Test
    public void shouldReturnXpmMetadataIfAllSet() {
        //given
        DefaultPageModel pageModel = new DefaultPageModel();
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
        DefaultPageModel pageModel = new DefaultPageModel();
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
        DefaultPageModel pageModel = new DefaultPageModel();

        //when
        pageModel.addExtensionData("key", "value");

        //then
        assertEquals("value", pageModel.getExtensionData().get("key"));
    }

    @Test
    public void shouldSetExtensionData() {
        //given
        DefaultPageModel pageModel = new DefaultPageModel();
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
    public void shouldSerializePageIdAsInteger() throws IOException {
        //given
        DefaultPageModel pageModel = new DefaultPageModel();
        pageModel.setId("1");
        ObjectMapper objectMapper = new DxaSpringInitialization().objectMapper();

        //when
        String asString = objectMapper.writeValueAsString(pageModel);

        //then
        Matcher matcher = Pattern.compile(".*\"Id\"[:\\s]*\"1\".*", Pattern.MULTILINE).matcher(asString
                .replace("\r", "").replace("\n", ""));
        assertTrue(matcher.matches());
    }

    @Test
    public void shouldRequestRegionsToFilterEntities() throws DxaException {
        //given
        DefaultPageModel page = new DefaultPageModel();

        RegionModelSetImpl regions = new RegionModelSetImpl();
        page.setRegions(regions);
        RegionModel regionModel = spy(new RegionModelImpl("1"));
        regions.add(regionModel);
        RegionModel regionModel2 = spy(new RegionModelImpl("2"));
        regions.add(regionModel2);

        ConditionalEntityEvaluator evaluator = mock(ConditionalEntityEvaluator.class);
        List<ConditionalEntityEvaluator> evaluators = Collections.singletonList(evaluator);

        //when
        page.filterConditionalEntities(evaluators);

        //then
        verify(regionModel).filterConditionalEntities(eq(evaluators));
        verify(regionModel2).filterConditionalEntities(eq(evaluators));
    }
}