package com.sdl.dxa.javadoc;

import com.sun.javadoc.Tag;
import com.sun.tools.doclets.Taglet;
import com.sun.tools.javadoc.ClassDocImpl;
import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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

        ClassDocImpl classDoc = mock(ClassDocImpl.class);
        PublicApiTaglet taglet = new PublicApiTaglet();
        Tag tag1 = mock(Tag.class);
        when(tag1.text()).thenReturn("tag1");
        when(tag1.holder()).thenReturn(classDoc);
        Tag tag2 = mock(Tag.class);
        when(tag2.text()).thenReturn("tag2");
        when(tag2.holder()).thenReturn(classDoc);

        //when

        //then
        assertEquals(String.format(template, "tag1"), taglet.toString(tag1));
        assertEquals(String.format(template, "<ul><li>tag1</li><li>tag2</li></ul>"), taglet.toString(new Tag[]{tag1, tag2}));

    }
}