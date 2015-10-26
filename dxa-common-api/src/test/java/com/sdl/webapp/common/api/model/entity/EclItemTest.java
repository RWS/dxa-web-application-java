package com.sdl.webapp.common.api.model.entity;

import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.util.StringUtils;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Objects;

import static org.junit.Assert.*;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EclItemTest {

    @Test
    public void shouldPutComponentIdToMetadataIfEclUriIsSet() {
        //given
        String uri = "uri",
                expectedXpmMarkup = "<!-- Start Component Presentation: {\"ComponentID\" : \"uri\", \"ComponentModified\" " +
                        ": \"null\", \"ComponentTemplateID\" : \"null\", \"ComponentTemplateModified\" : \"null\", " +
                        "\"IsRepositoryPublished\" : false} -->";

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

    @Test
    public void shouldReadSpecificEclDataFromXHtmlNode() {
        //given
        Node xhtmlNode = mock(Node.class);
        EclItem eclItem = new EclItem() { };
        NamedNodeMap map = mock(NamedNodeMap.class);

        when(xhtmlNode.getAttributes()).thenReturn(map);
        when(map.getNamedItem(anyString())).thenAnswer(new Answer<Node>() {
            @Override
            public Node answer(InvocationOnMock invocation) throws Throwable {
                Node node = mock(Node.class);
                String result = Objects.toString(invocation.getArguments()[0]);
                switch (result) {
                    case "xlink:href":
                        result = "0-1";
                        break;
                    case "data-multimediaFileSize":
                        result = "1024";
                        break;
                    default:
                        break;
                }

                when(node.getNodeValue()).thenReturn(result);
                return node;
            }
        });

        //when
        eclItem.readFromXhtmlElement(xhtmlNode);

        //then
        assertEquals("data-eclId", eclItem.getUri());
        assertEquals("data-eclDisplayTypeId", eclItem.getDisplayTypeId());
        assertEquals("data-eclTemplateFragment", eclItem.getTemplateFragment());
        assertEquals("data-eclFileName", eclItem.getFileName());
        assertEquals("data-eclMimeType", eclItem.getMimeType());
    }
}