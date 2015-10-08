package com.sdl.webapp.common.api.model.entity;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.StringUtils;

import java.util.HashMap;

import static org.junit.Assert.*;

public class EclItemTest {

    @Test
    public void shouldPutComponentIdToMetadataIfEclUriIsSet() {
        //given
        String uri = "uri",
                expectedXpmMarkup = "<!-- Start Component Presentation: {\"ComponentID\" : \"uri\", \"ComponentModified\" " +
                        ": \"null\", \"ComponentTemplateID\" : \"null\", \"ComponentTemplateModified\" : \"null\", " +
                        "\"IsRepositoryPublished\" : null} -->";

        EclItem eclItem = new EclItem() { };
        eclItem.setUri(uri);
        eclItem.setXpmMetadata(new HashMap<String, String>());

        //when
        String resultXpmMarkup = eclItem.getXpmMarkup(null);

        //then
        assertEquals(uri, eclItem.getXpmMetadata().get(EclItem.COMPONENT_ID_KEY));
        assertEquals(expectedXpmMarkup, resultXpmMarkup);
    }

    @Test
    public void shouldReturnEmptyStringIfXpmMetadataIsNull() {
        //given
        EclItem eclItem = new EclItem() { };

        //when
        String xpmMarkup = eclItem.getXpmMarkup(null);

        //then
        assertTrue(StringUtils.isEmpty(xpmMarkup));
    }

    @Test
    public void shouldReturnTemplateFragmentWhenToHtmlIsCalled() {
        //given
        String templateFragment = "templateFragment";
        EclItem eclItem = new EclItem() { };
        eclItem.setTemplateFragment(templateFragment);

        //when
        String toHtml = eclItem.toHtml();
        String toHtml1 = eclItem.toHtml("100%");
        String toHtml2 = eclItem.toHtml("100%", 0.0, "", 0);

        //then
        assertNotNull(toHtml);
        assertEquals(templateFragment, toHtml);
        assertEquals(toHtml, toHtml1);
        assertEquals(toHtml1, toHtml2);
    }
}