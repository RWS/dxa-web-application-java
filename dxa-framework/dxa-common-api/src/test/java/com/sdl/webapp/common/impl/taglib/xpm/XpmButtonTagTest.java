package com.sdl.webapp.common.impl.taglib.xpm;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.markup.html.HtmlNode;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.HashMap;

import static com.sdl.webapp.common.util.XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY;
import static com.sdl.webapp.common.util.XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY;
import static com.sdl.webapp.common.util.XpmUtils.RegionXpmBuilder.INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = XpmButtonTagTest.SpringConfigurationContext.class)
@ActiveProfiles("test")
public class XpmButtonTagTest {

    @Test
    public void shouldGenerateXpmButtonHtmlForNonInclude() {
        //given 
        RegionModel region = mock(RegionModel.class);
        doReturn(new HashMap<String, Object>() {{
            put(INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY, "INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY");
            put(INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY, "INCLUDED_FROM_PAGE_FILE_NAME_XPM_METADATA_KEY");
            put(INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY, "INCLUDED_FROM_PAGE_ID_XPM_METADATA_KEY");
        }}).when(region).getXpmMetadata();

        XpmButtonTag tag = new XpmButtonTag();
        tag.setRegion(region);
        tag.setCssClass("cssClass");

        //when
        HtmlNode markup = tag.generateXpmMarkup();

        //then
        assertEquals("<div class=\"xpm-button cssClass\">" +
                "<a href=\"localized-path\" class=\"fa-stack fa-lg\" title=\"Edit INCLUDED_FROM_PAGE_TITLE_XPM_METADATA_KEY\">" +
                "<i class=\"fa fa-square fa-stack-2x\"></i>" +
                "<i class=\"fa fa-pencil fa-inverse fa-stack-1x\"></i>" +
                "</a>" +
                "</div>", markup.renderHtml());
    }


    @Test
    public void shouldGenerateXpmButtonHtmlForInclude() {
        //given
        RegionModel region = mock(RegionModel.class);
        doReturn(new HashMap<String, Object>()).when(region).getXpmMetadata();

        XpmButtonTag tag = new XpmButtonTag();
        tag.setRegion(region);
        tag.setCssClass("cssClass");

        //when
        HtmlNode markup = tag.generateXpmMarkup();

        //then
        assertEquals("<div class=\"xpm-button cssClass\">" +
                "<a href=\"javascript:history.back()\" class=\"fa-stack fa-lg\" title=\"Go Back\">" +
                "<i class=\"fa fa-square fa-stack-2x\"></i>" +
                "<i class=\"fa fa-arrow-left fa-inverse fa-stack-1x\"></i>" +
                "</a>" +
                "</div>", markup.renderHtml());
    }

    @Test
    public void shouldDetectWhenCssClassIsNotSet() {
        //given
        RegionModel region = mock(RegionModel.class);
        doReturn(new HashMap<String, Object>()).when(region).getXpmMetadata();

        XpmButtonTag tag = new XpmButtonTag();
        tag.setRegion(region);

        //when
        HtmlNode markup = tag.generateXpmMarkup();

        //then
        String html = markup.renderHtml();
        assertTrue(html.startsWith("<div class=\"xpm-button\">"));
    }

    @Configuration
    public static class SpringConfigurationContext {

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @Bean
        public WebRequestContext webRequestContext() {
            Localization localization = mock(Localization.class);
            when(localization.localizePath(anyString())).thenReturn("localized-path");

            WebRequestContext mock = mock(WebRequestContext.class);
            when(mock.getLocalization()).thenReturn(localization);
            return mock;
        }
    }

}