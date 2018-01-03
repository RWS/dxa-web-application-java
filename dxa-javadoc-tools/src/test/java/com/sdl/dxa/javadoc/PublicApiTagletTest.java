package com.sdl.dxa.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class PublicApiTagletTest {

    @Test
    public void shouldRegisterTaglet() {
        //given 
        Map<String, Taglet> tagletMap = new HashMap<>();

        //when
        PublicApiTaglet.register(tagletMap);

        //then
        TestCase.assertTrue(tagletMap.containsKey(Constants.PUBLIC_API_TAG));
        TestCase.assertTrue(tagletMap.get(Constants.PUBLIC_API_TAG) instanceof PublicApiTaglet);
    }

    @Test
    public void shouldReturnCorrectName() {
        //given 

        //when
        String name = new PublicApiTaglet().getName();

        //then
        Assert.assertEquals(Constants.PUBLIC_API_TAG, name);
    }

    @Test
    public void shouldSupportAllTypesOfElements() {
        //given 
        PublicApiTaglet taglet = new PublicApiTaglet();

        //when

        //then
        assertTrue(taglet.inConstructor());
        assertTrue(taglet.inField());
        assertTrue(taglet.inMethod());
        assertTrue(taglet.inOverview());
        assertTrue(taglet.inPackage());
        assertTrue(taglet.inType());

        assertFalse(taglet.isInlineTag());
    }

    @Test
    public void shouldGenerateCorrectHtml() {
        //given
        String header = "This is DXA Public API";
        String template = String.format("<dt><strong>%s</strong><dd><table cellpadding=2 cellspacing=0><tr><td>%s</td></tr></table></dd></dt>", header, "%s");

        PublicApiTaglet taglet = new PublicApiTaglet();
        Tag tag1 = Mockito.mock(Tag.class);
        Mockito.when(tag1.text()).thenReturn("tag1");
        Tag tag2 = Mockito.mock(Tag.class);
        Mockito.when(tag2.text()).thenReturn("tag2");

        //when

        //then
        assertEquals(String.format(template, "tag1"), taglet.toString(tag1));
        assertEquals(String.format(template, "<ul><li>tag1</li><li>tag2</li></ul>"), taglet.toString(new Tag[]{tag1, tag2}));

    }
}