package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.convert.TypeDescriptor;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RichTextDataConverterTest {

    @Mock
    private LinkResolver linkResolver;

    @Mock
    private WebRequestContext webRequestContext;

    @Mock
    private Localization localization;

    @InjectMocks
    private RichTextDataConverter converter;

    @Before
    public void init() {
        when(webRequestContext.getLocalization()).thenReturn(localization);

        when(localization.getId()).thenReturn("1");

        when(linkResolver.resolveLink(eq("tcm:1-2"), eq("1"))).thenReturn("");
        when(linkResolver.resolveLink(eq("tcm:1-3"), eq("1"))).thenReturn("");
        when(linkResolver.resolveLink(eq("tcm:1-11"), eq("1"))).thenReturn("resolved-link");
    }

    @Test
    public void shouldSuppressBrokenLinks() {
        //given 
        RichTextData data = new RichTextData(Lists.newArrayList("<p>Text <a href=\"tcm:1-2\">link text</a><!--CompLink tcm:1-2--> text ",
                "<a href=\"tcm:1-3\">link2",
                " text2",
                "</a><!--CompLink tcm:1-3--></p>",
                "<a href=\"tcm:1-2\">text3</a><!--CompLink tcm:1-2-->"));
        TypeDescriptor targetType = mock(TypeDescriptor.class);
        doReturn(String.class).when(targetType).getObjectType();

        //when
        Object result = converter.convert(data, targetType, null, null);

        //then
        assertEquals("<p>Text link text text link2 text2</p>text3", result);
    }

    @Test
    public void shouldResolveLinks_IfSameTcmUri_IsDoubledInSameFragment() {
        //given
        RichTextData data = new RichTextData(Lists.newArrayList("<p>Text <a href=\"tcm:1-2\">link text</a><!--CompLink tcm:1-2--> " +
                "<a href=\"tcm:1-2\">link text</a><!--CompLink tcm:1-2--> text </p>"));
        TypeDescriptor targetType = mock(TypeDescriptor.class);
        doReturn(String.class).when(targetType).getObjectType();

        //when
        Object result = converter.convert(data, targetType, null, null);

        //then
        assertEquals("<p>Text link text link text text </p>", result);
    }

    @Test
    public void shouldResolveLinks_AndRemoveMarkers() {
        RichTextData data = new RichTextData(Lists.newArrayList("<p>Text <a href=\"tcm:1-11\">link text</a><!--CompLink tcm:1-11--></p>"));
        TypeDescriptor targetType = mock(TypeDescriptor.class);
        doReturn(String.class).when(targetType).getObjectType();

        //when
        Object result = converter.convert(data, targetType, null, null);

        //then
        assertEquals("<p>Text <a href=\"resolved-link\">link text</a></p>", result);
    }
}