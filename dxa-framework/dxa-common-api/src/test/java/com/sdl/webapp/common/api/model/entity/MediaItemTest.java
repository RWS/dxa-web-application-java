package com.sdl.webapp.common.api.model.entity;

import com.google.common.collect.ImmutableMap;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MediaItemTest {

    static NamedNodeMap mockAttributesWithDefault(final Map<String, String> map) {
        NamedNodeMap namedNodeMap = mock(NamedNodeMap.class);

        when(namedNodeMap.getNamedItem(anyString())).thenAnswer(new Answer<Node>() {
            @Override
            public Node answer(InvocationOnMock invocation) throws Throwable {
                Node node = mock(Node.class);
                String key = (String) invocation.getArguments()[0];
                when(node.getNodeValue()).thenReturn(map.containsKey(key) ? map.get(key) : key);
                return node;
            }
        });

        return namedNodeMap;
    }

    @Test
    public void shouldReadFromXhtmlElement() throws Exception {
        //given
        MediaItem mediaItem = new MediaItemTestImpl();
        Node node = mock(Node.class);

        NamedNodeMap namedNodeMap = mockAttributesWithDefault(ImmutableMap.<String, String>builder()
                .put("xlink:href", "0-1")
                .put("data-multimediaFileSize", "42").build());
        when(node.getAttributes()).thenReturn(namedNodeMap);

        //when
        mediaItem.readFromXhtmlElement(node);

        //then
        assertEquals("1", mediaItem.getId());
        assertEquals("src", mediaItem.getUrl());
        assertEquals("class", mediaItem.getHtmlClasses());
        assertEquals("data-multimediaFileName", mediaItem.getFileName());
        assertEquals("data-multimediaMimeType", mediaItem.getMimeType());
        assertEquals(42, mediaItem.getFileSize());
        assertTrue(mediaItem.isEmbedded());
    }

    private static class MediaItemTestImpl extends MediaItem {

        @Override
        public HtmlElement toHtmlElement(String widthFactor) throws DxaException {
            return null;
        }

        @Override
        public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize) throws DxaException {
            return null;
        }

        @Override
        public HtmlElement toHtmlElement(String widthFactor, double aspect, String cssClass, int containerSize, String contextPath) throws DxaException {
            return null;
        }
    }
}