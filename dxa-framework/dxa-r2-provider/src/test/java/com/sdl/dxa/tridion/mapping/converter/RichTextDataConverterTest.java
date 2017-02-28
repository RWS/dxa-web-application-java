package com.sdl.dxa.tridion.mapping.converter;

import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.tridion.fields.exceptions.FieldConverterException;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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
    public void shouldSuppressBrokenLinks() throws FieldConverterException {
        //given 
        RichTextData data = new RichTextData(Lists.newArrayList("<p>Text <a href=\"tcm:1-2\">link text</a><!--CompLink tcm:1-2--> text ",
                "<a href=\"tcm:1-3\">link2",
                " text2",
                "</a><!--CompLink tcm:1-3--></p>",
                "<a href=\"tcm:1-2\">text3</a><!--CompLink tcm:1-2-->"));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();

        //when
        Object result = converter.convert(data, typeInformation, null, null, null);

        //then
        assertEquals("<p>Text link text text link2 text2</p>text3", result);
    }

    @Test
    public void shouldResolveLinks_IfSameTcmUri_IsDoubledInSameFragment() throws FieldConverterException {
        //given
        RichTextData data = new RichTextData(Lists.newArrayList("<p>Text <a href=\"tcm:1-2\">link text</a><!--CompLink tcm:1-2--> " +
                "<a href=\"tcm:1-2\">link text</a><!--CompLink tcm:1-2--> text </p>"));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();

        //when
        Object result = converter.convert(data, typeInformation, null, null, null);

        //then
        assertEquals("<p>Text link text link text text </p>", result);
    }

    @Test
    public void shouldResolveLinks_AndRemoveMarkers() throws FieldConverterException {
        RichTextData data = new RichTextData(Lists.newArrayList("<p>Text <a href=\"tcm:1-11\">link text</a><!--CompLink tcm:1-11--></p>"));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();

        //when
        Object result = converter.convert(data, typeInformation, null, null, null);

        //then
        assertEquals("<p>Text <a href=\"resolved-link\">link text</a></p>", result);
    }

    @Test
    public void shouldResolveLinks_InFragmentWithLineBreaks() throws FieldConverterException {
        RichTextData data = new RichTextData(Lists.newArrayList("<p>\nText <a href=\"tcm:1-11\">\nlink text</a><!--CompLink tcm:1-11-->\n</p>"));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();

        //when
        Object result = converter.convert(data, typeInformation, null, null, null);

        //then
        assertEquals("<p>\nText <a href=\"resolved-link\">\nlink text</a>\n</p>", result);
    }

    @Test
    public void shouldResolveLinks_WhenLinkHasManyAttrs() throws FieldConverterException {
        RichTextData data = new RichTextData(Lists.newArrayList("<p>\nText <a data-first=\"1\" href=\"tcm:1-11\" data-second=\"2\">\nlink text</a><!--CompLink tcm:1-11-->\n</p>"));
        TypeInformation typeInformation = TypeInformation.builder().objectType(String.class).build();

        //when
        Object result = converter.convert(data, typeInformation, null, null, null);

        //then
        assertEquals("<p>\nText <a data-first=\"1\" href=\"resolved-link\" data-second=\"2\">\nlink text</a>\n</p>", result);
    }
}