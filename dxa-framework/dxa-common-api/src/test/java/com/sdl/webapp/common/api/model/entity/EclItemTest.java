package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.sdl.dxa.DxaSpringInitialization;
import com.sdl.webapp.common.api.MediaHelper;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.sdl.webapp.common.api.model.entity.MediaItemTest.mockAttributesWithDefault;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
public class EclItemTest {

    @Autowired
    private ObjectMapper objectMapper;

    private static String removeXmlFromXpmString(String expectedXpmMarkup) {
        return expectedXpmMarkup.replaceFirst("<!-- Start Component Presentation: ", "").replaceFirst("-->", "");
    }

    @Test
    public void shouldPutComponentIdToMetadataIfEclUriIsSet() throws IOException {
        //given
        String uri = "uri",
                expectedXpmMarkup = "<!-- Start Component Presentation: { \"ComponentID\" : \"uri\" } -->";

        EclItem eclItem = new EclItem() {
        };
        eclItem.setUri(uri);
        eclItem.setXpmMetadata(new HashMap<String, Object>());

        //when
        String resultXpmMarkup = eclItem.getXpmMarkup(null);

        //then
        assertEquals(uri, eclItem.getXpmMetadata().get(EclItem.COMPONENT_ID_KEY));
        assertTrue(resultXpmMarkup.startsWith("<!-- Start Component Presentation: "));
        assertTrue(resultXpmMarkup.endsWith("-->"));
        assertEquals(readJsonToMap(removeXmlFromXpmString(expectedXpmMarkup)),
                readJsonToMap(removeXmlFromXpmString(resultXpmMarkup)));
    }

    private Map<String, String> readJsonToMap(String str) throws IOException {
        return objectMapper.readValue(str, new TypeReference<HashMap<String, String>>() {
        });
    }

    @Test
    public void shouldReturnEmptyStringIfXpmMetadataIsNull() {
        //given
        EclItem eclItem = new EclItem() {
        };

        //when
        String xpmMarkup = eclItem.getXpmMarkup(null);

        //then
        assertTrue(StringUtils.isEmpty(xpmMarkup));
    }

    @Test
    public void shouldReturnTemplateFragmentWhenToHtmlIsCalled() throws DxaException {
        //given
        String templateFragment = "templateFragment";
        EclItem eclItem = new EclItem() {
        };
        eclItem.setTemplateFragment(templateFragment);

        //when
        String toHtml = eclItem.toHtmlElement().toHtml();
        String toHtml1 = eclItem.toHtmlElement("100%").toHtml();
        String toHtml2 = eclItem.toHtmlElement("100%", 0.0, "", 0).toHtml();

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
        EclItem eclItem = new EclItem() {
        };
        NamedNodeMap map = mockAttributesWithDefault(ImmutableMap.<String, String>builder()
                .put("xlink:href", "0-1")
                .put("data-multimediaFileSize", "1024")
                .build());

        when(xhtmlNode.getAttributes()).thenReturn(map);

        //when
        eclItem.readFromXhtmlElement(xhtmlNode);

        //then
        assertEquals("data-eclId", eclItem.getUri());
        assertEquals("data-eclDisplayTypeId", eclItem.getDisplayTypeId());
        assertEquals("data-eclTemplateFragment", eclItem.getTemplateFragment());
        assertEquals("data-eclFileName", eclItem.getFileName());
        assertEquals("data-eclMimeType", eclItem.getMimeType());
    }

    @Test
    public void shouldReturnFromExternalMetadata() {
        //given
        EclItem eclItem = new EclItem() {
            @Override
            public Map<String, Object> getExternalMetadata() {
                return ImmutableMap.of("Test1", "Test1_v", "Test2", "Test2_v");
            }
        };

        //when
        Object o1 = eclItem.getFromExternalMetadataOrAlternative("Test1", "Test1_alt");
        Object o2 = eclItem.getFromExternalMetadataOrAlternative("Test2", "Test2_alt");
        Object o3 = eclItem.getFromExternalMetadataOrAlternative("Test3", "Test3_alt");

        //then
        assertEquals("Test1_v", o1);
        assertEquals("Test2_v", o2);
        assertEquals("Test3_alt", o3);
    }

    @Test
    public void shouldReturnFromNestedMapInExternalMetadata() {
        //given
        EclItem eclItem = new EclItem() {
            @Override
            public Map<String, Object> getExternalMetadata() {
                return ImmutableMap.of("Test1", ImmutableMap.of("_Test1", "Value",
                        "Test1_2", ImmutableMap.of("_Test1_2", "_Value")));
            }
        };

        //when
        Object o0 = eclItem.getFromExternalMetadataOrAlternative("Test1/_Test1", "Alt");
        Object o1 = eclItem.getFromExternalMetadataOrAlternative("Test1/Test1_2/_Test1_2", "Alt");
        Object o2 = eclItem.getFromExternalMetadataOrAlternative("Test1/_Test2", "Alt2");
        Object o3 = eclItem.getFromExternalMetadataOrAlternative("Test2/_Test2", "Alt3");
        Object o4 = eclItem.getFromExternalMetadataOrAlternative(null, "Alt4");

        //then
        assertEquals("Value", o0);
        assertEquals("_Value", o1);
        assertEquals("Alt2", o2);
        assertEquals("Alt3", o3);
        assertEquals("Alt4", o4);
    }

    @org.springframework.context.annotation.Configuration
    @Profile("test")
    static class SpringContext {

        @Bean
        public ApplicationContextHolder applicationContextHolder() {
            return new ApplicationContextHolder();
        }

        @SuppressWarnings("Duplicates")
        @Bean
        public ObjectMapper objectMapper() {
            return new DxaSpringInitialization().objectMapper();
        }

        @Bean
        public MediaHelper.MediaHelperFactory mediaHelperFactory() {
            return mock(MediaHelper.MediaHelperFactory.class);
        }
    }
}