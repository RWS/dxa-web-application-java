package com.sdl.webapp.common.api.model.page;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModelSet;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataCreator;
import com.sdl.webapp.common.api.model.region.RegionModelImpl;
import com.sdl.webapp.common.api.model.region.RegionModelSetImpl;
import com.sdl.webapp.common.exceptions.DxaException;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
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
        AbstractPageModelImpl pageModel = new PageModelImpl();
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
        AbstractPageModelImpl pageModel = new PageModelImpl();

        //when
        pageModel.setXpmMetadata(null);

        //then
        //NPE
    }

    @Test
    public void shouldNeverHaveNullXpmMetadata() {
        //given
        AbstractPageModelImpl pageModel = new PageModelImpl();

        //when

        //then
        assertNotNull(pageModel.getXpmMetadata());
    }

    @Test
    public void shouldReturnXpmMetadataIfAllSet() {
        //given
        AbstractPageModelImpl pageModel = new PageModelImpl();
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
        AbstractPageModelImpl pageModel = new PageModelImpl();
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
        AbstractPageModelImpl pageModel = new PageModelImpl();
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
        AbstractPageModelImpl pageModel2 = new PageModelImpl(pageModel);

        //then
        assertFalse(pageModel == pageModel2);
        assertEquals(pageModel, pageModel2);
    }
}