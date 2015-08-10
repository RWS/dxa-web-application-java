package com.sdl.webapp.main.taglib.xpm;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.markup.html.HtmlCommentNode;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@code XpmPropertyMarkupTag}.
 */
public class XpmPropertyMarkupTagTest {

    @Test
    public void testGenerateXpmMarkup() {
        final HtmlNode expected = new HtmlCommentNode("Start Component Field: {\"XPath\":\"/some/fake/xpath[1]\"}");
        assertThat(setup("example", "/some/fake/xpath", 0).generateXpmMarkup(), is(expected));
    }

    @Test
    public void testGenerateXpmMarkupPredefinedIndex() {
        final HtmlNode expected = new HtmlCommentNode("Start Component Field: {\"XPath\":\"/some/fake/xpath[23]\"}");
        assertThat(setup("example", "/some/fake/xpath[23]", 0).generateXpmMarkup(), is(expected));
    }

    private XpmPropertyMarkupTag setup(String propertyName, String xpath, int index) {
        final Entity entity = mock(Entity.class);
        when(entity.getPropertyData()).thenReturn(ImmutableMap.of(propertyName, xpath));

        final XpmPropertyMarkupTag tag = new XpmPropertyMarkupTag();
        tag.setEntity(entity);
        tag.setProperty(propertyName);
        tag.setIndex(index);

        return tag;
    }
}
