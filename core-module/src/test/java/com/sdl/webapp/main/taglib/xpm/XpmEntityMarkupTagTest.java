package com.sdl.webapp.main.taglib.xpm;

import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code XpmEntityMarkupTag}.
 */
public class XpmEntityMarkupTagTest {

    @Test
    public void testGenerateXpmMarkup() {
        final Map<String, String> entityData = new HashMap<>();
        entityData.put("ComponentID", "C123");
        entityData.put("ComponentModified", "2014-08-09T17:55:00+01:00");
        entityData.put("ComponentTemplateID", "T456");
        entityData.put("ComponentTemplateModified", "2014-06-04T23:55:00+01:00");

        final HtmlNode expected = new HtmlCommentNode("Start Component Presentation: {\"ComponentID\":\"C123\"," +
                "\"ComponentModified\":\"2014-08-09T17:55:00+01:00\",\"ComponentTemplateID\":\"T456\"," +
                "\"ComponentTemplateModified\":\"2014-06-04T23:55:00+01:00\",\"IsRepositoryPublished\":false}");

        assertThat(setup(entityData).generateXpmMarkup(), is(expected));
    }

    @Test
    public void testGenerateXpmMarkupForQuery() {
        final Map<String, String> entityData = new HashMap<>();
        entityData.put("ComponentID", "C123");
        entityData.put("ComponentModified", "2014-08-09T17:55:00+01:00");
        entityData.put("ComponentTemplateID", "tcm:0-0-0");
        entityData.put("ComponentTemplateModified", "2014-06-04T23:55:00+01:00");

        final HtmlNode expected = new HtmlCommentNode("Start Component Presentation: {\"ComponentID\":\"C123\"," +
                "\"ComponentModified\":\"2014-08-09T17:55:00+01:00\",\"ComponentTemplateID\":\"tcm:0-0-0\"," +
                "\"ComponentTemplateModified\":\"2014-06-04T23:55:00+01:00\",\"IsRepositoryPublished\":true," +
                "\"IsQueryBased\":true}");

        assertThat(setup(entityData).generateXpmMarkup(), is(expected));
    }

    private XpmEntityMarkupTag setup(Map<String, String> entityData) {
        final Entity entity = mock(Entity.class);
        when(entity.getEntityData()).thenReturn(entityData);

        final XpmEntityMarkupTag tag = new XpmEntityMarkupTag();
        tag.setEntity(entity);

        return tag;
    }
}
