package com.sdl.webapp.util.dd4t;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.model.MvcData;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.PageTemplate;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.FieldSetImpl;
import org.dd4t.contentmodel.impl.TextField;
import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MvcDataHelperTest {

    @Test
    public void shouldCreateMvcDataForPageTemplate() {
        //given 
        PageTemplate pageTemplate = mock(PageTemplate.class);

        when(pageTemplate.getMetadata()).thenReturn(new HashMap<String, Field>() {{
            TextField viewField = new TextField();
            viewField.setTextValues(Lists.newArrayList("ANT:VNT"));
            put("view", viewField);

            TextField metaField = new TextField();
            metaField.setTextValues(Lists.newArrayList("META"));
            put("meta1", metaField);

            EmbeddedField embeddedField = new EmbeddedField();
            FieldSet fieldSet = new FieldSetImpl();
            fieldSet.setContent(new HashMap<String, Field>() {{
                TextField embed = new TextField();
                embed.setTextValues(Lists.newArrayList("EMBED"));
                put("embed1", embed);

                TextField embed2 = new TextField();
                put("embed2", embed2); //to be ignored
            }});
            embeddedField.setEmbeddedValues(Lists.newArrayList(fieldSet));
            put("embedded", embeddedField);
        }});

        //when
        MvcData mvcData = MvcDataHelper.createPageMvcData(pageTemplate);

        //then
        assertEquals("ANT", mvcData.getAreaName());
        assertEquals("VNT", mvcData.getViewName());
        assertEquals("Framework", mvcData.getControllerAreaName());
        assertEquals("Page", mvcData.getControllerName());
        assertEquals("META", mvcData.getMetadata().get("meta1"));
        //noinspection unchecked
        assertTrue(((List<Map<String, String>>) mvcData.getMetadata().get("embedded")).size() == 1); // one embed is empty => ignored
        assertTrue(mvcData.getMetadata().size() == 2); // view meta is not doubled, so in meta we have embed and meta1
    }

    @Test
    public void shouldCreateMvcDataForPageTemplate_FallbackScenario() {
        //given
        PageTemplate pageTemplate = mock(PageTemplate.class);
        when(pageTemplate.getTitle()).thenReturn("Title With Spaces");
        when(pageTemplate.getMetadata()).thenReturn(Collections.emptyMap());

        //when
        MvcData mvcData = MvcDataHelper.createPageMvcData(pageTemplate);

        //then
        assertEquals("Core", mvcData.getAreaName());
        assertEquals("TitleWithSpaces", mvcData.getViewName());
        assertEquals("Framework", mvcData.getControllerAreaName());
        assertEquals("Page", mvcData.getControllerName());
    }

    @Test
    public void shouldCreateMvcDataForRegion() {
        //given 
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(new HashMap<String, Field>() {{
            TextField viewField = new TextField();
            viewField.setTextValues(Lists.newArrayList("ANT:VNT"));
            put("regionView", viewField);
        }});

        //when
        MvcData mvcData = MvcDataHelper.createRegionMvcData(componentTemplate);

        //then
        assertEquals("ANT", mvcData.getAreaName());
        assertEquals("VNT", mvcData.getViewName());
        assertEquals("Framework", mvcData.getControllerAreaName());
        assertEquals("Region", mvcData.getControllerName());
    }

    @Test
    public void shouldFallbackToDefaultsWithBrackets() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(Collections.emptyMap());
        when(componentTemplate.getTitle()).thenReturn("Hello[AreaName:ViewName]");

        //when
        MvcData mvcData = MvcDataHelper.createRegionMvcData(componentTemplate);

        //then
        assertEquals("AreaName", mvcData.getAreaName());
        assertEquals("ViewName", mvcData.getViewName());
        assertEquals("Framework", mvcData.getControllerAreaName());
        assertEquals("Region", mvcData.getControllerName());
    }

    @Test
    public void shouldFallbackToDefaultsWithoutBrackets() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(Collections.emptyMap());
        when(componentTemplate.getTitle()).thenReturn("DoesntMatchRegexpWithBrackets");

        //when
        MvcData mvcData = MvcDataHelper.createRegionMvcData(componentTemplate);

        //then
        assertEquals("Core", mvcData.getAreaName());
        assertEquals("Main", mvcData.getViewName());
        assertEquals("Framework", mvcData.getControllerAreaName());
        assertEquals("Region", mvcData.getControllerName());
    }

    @Test
    public void shouldCreateMvcDataForComponentPresentation() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(new HashMap<String, Field>() {{
            TextField controller = new TextField();
            controller.setTextValues(Lists.newArrayList("AreaController:ViewController"));
            put("controller", controller);

            TextField view = new TextField();
            view.setTextValues(Lists.newArrayList("AreaView:ViewView"));
            put("view", view);

            TextField region = new TextField();
            region.setTextValues(Lists.newArrayList("AreaRegion:ViewRegion"));
            put("regionView", region);

            TextField action = new TextField();
            action.setTextValues(Lists.newArrayList("Action"));
            put("action", action);

            TextField routeValues = new TextField();
            routeValues.setTextValues(Lists.newArrayList("Route1:To1,Route2:To2,Route2:WrongTo2,Route3"));
            put("routeValues", routeValues);

            TextField meta1 = new TextField();
            meta1.setTextValues(Lists.newArrayList("Meta1"));
            put("meta1", meta1);

            TextField meta2 = new TextField();
            put("meta2", meta2); //to ignore
        }});

        ComponentPresentation componentPresentation = mock(ComponentPresentation.class);
        when(componentPresentation.getComponentTemplate()).thenReturn(componentTemplate);

        //when
        MvcData mvcData = MvcDataHelper.createMvcData(componentPresentation);

        //then
        assertEquals("AreaController", mvcData.getControllerAreaName());
        assertEquals("ViewController", mvcData.getControllerName());
        assertEquals("AreaView", mvcData.getAreaName());
        assertEquals("ViewView", mvcData.getViewName());
        assertEquals("AreaRegion", mvcData.getRegionAreaName());
        assertEquals("ViewRegion", mvcData.getRegionName());
        assertEquals("Action", mvcData.getActionName());
        assertEquals("Meta1", mvcData.getMetadata().get("meta1"));
        assertTrue(mvcData.getMetadata().size() == 1);
        Map<String, String> routeValues = mvcData.getRouteValues();
        assertEquals("To1", routeValues.get("Route1"));
        assertEquals("To2", routeValues.get("Route2"));
        assertTrue(routeValues.size() == 2);
    }

    @Test
    public void shouldCreateMvcDataForComponentPresentation_FallbackScenario() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(Collections.emptyMap());
        when(componentTemplate.getTitle()).thenReturn("My Title With Spaces");

        ComponentPresentation componentPresentation = mock(ComponentPresentation.class);
        when(componentPresentation.getComponentTemplate()).thenReturn(componentTemplate);

        //when
        MvcData mvcData = MvcDataHelper.createMvcData(componentPresentation);

        //then
        assertEquals("Framework", mvcData.getControllerAreaName());
        assertEquals("Entity", mvcData.getControllerName());
        assertEquals("Core", mvcData.getAreaName());
        assertEquals("MyTitleWithSpaces", mvcData.getViewName());
        assertEquals("Core", mvcData.getRegionAreaName());
        assertEquals("Main", mvcData.getRegionName());
        assertEquals("Entity", mvcData.getActionName());
    }

    @Test
    public void shouldGetRegionNameFromCP() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(new HashMap<String, Field>() {{
            TextField regionName = new TextField();
            regionName.setTextValues(Lists.newArrayList("RegionName"));
            put("regionName", regionName);
        }});

        ComponentPresentation componentPresentation = mock(ComponentPresentation.class);
        when(componentPresentation.getComponentTemplate()).thenReturn(componentTemplate);

        //when
        String regionName = MvcDataHelper.getRegionName(componentPresentation);

        //then
        assertEquals("RegionName", regionName);
    }

    @Test
    public void shouldFallbackToRegionViewInsteadOfRegionNameFromCP() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(new HashMap<String, Field>() {{
            TextField regionView = new TextField();
            regionView.setTextValues(Lists.newArrayList("RegionView"));
            put("regionView", regionView);
        }});

        ComponentPresentation componentPresentation = mock(ComponentPresentation.class);
        when(componentPresentation.getComponentTemplate()).thenReturn(componentTemplate);

        //when
        String regionName = MvcDataHelper.getRegionName(componentPresentation);

        //then
        assertEquals("RegionView", regionName);
    }

    @Test
    public void shouldFallbackToDefaultInsteadOfRegionName() {
        //given
        ComponentTemplate componentTemplate = mock(ComponentTemplate.class);
        when(componentTemplate.getMetadata()).thenReturn(Collections.emptyMap());

        ComponentPresentation componentPresentation = mock(ComponentPresentation.class);
        when(componentPresentation.getComponentTemplate()).thenReturn(componentTemplate);

        //when
        String regionName = MvcDataHelper.getRegionName(componentPresentation);

        //then
        assertEquals("Main", regionName);
    }

}