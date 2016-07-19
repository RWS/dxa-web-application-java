package com.sdl.webapp.common.impl.localization;

import com.google.common.collect.Lists;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.config.EntitySemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.FieldSemantics;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticField;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.impl.localization.LocalizationImpl.Builder;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.util.ReflectionTestUtils.getField;

public class LocalizationImplTest {

    @Test
    public void shouldReturnId() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setId("id");
        Localization local = builder.build();

        //then
        assertEquals("id", local.getId());
    }

    @Test
    public void shouldReturnPath() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setPath("root/etc");
        Localization local = builder.build();

        //then
        assertEquals("root/etc", local.getPath());
    }

    @Test
    public void shouldReturnLocalizedPath() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();
        Builder builder2 = getBuilder();

        //when
        builder.setPath("root/etc/");
        builder2.setPath("/root/etc");
        Localization local = builder.build();
        Localization local2 = builder2.build();

        //then
        assertEquals("root/etc/url", local.localizePath("url"));
        assertEquals("/root/etc/url", local2.localizePath("url"));
    }

    @Test
    public void shouldReturnStaticContent() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setPath("/path");
        builder.setMediaRoot("/path/media");
        Localization local = builder.build();

        //then
        assertFalse(local.isStaticContent("url"));
        assertFalse(local.isStaticContent("root/etc/test"));
        assertTrue(local.isStaticContent("/path/media"));
        assertFalse(local.isStaticContent("/path/test"));
    }

    @Test
    public void shouldReturnMedia() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setMediaRoot("home/media/");
        builder.setPath("/root");
        Localization local = builder.build();

        //then
        assertEquals("/root/home/media/", getField(local, "mediaRoot"));
    }

    @Test
    public void shouldReturnDefault() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setDefault(true);
        Localization local = builder.build();

        //then
        assertTrue(local.isDefault());
    }

    @Test
    public void shouldReturnStaging() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setStaging(true);
        Localization local = builder.build();

        //then
        assertTrue(local.isStaging());
    }

    @Test
    public void shouldReturnVersion() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.setVersion("2.0");
        Localization local = builder.build();

        //then
        assertEquals("2.0", local.getVersion());
    }

    @Test
    public void shouldReturnSchemas() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        EntitySemantics semantics = new EntitySemantics(new SemanticVocabulary("semantic"), "entity");
        SemanticSchema schema = new SemanticSchema(1L, "root", Collections.singleton(semantics), new HashMap<FieldSemantics, SemanticField>());
        SemanticSchema schema2 = new SemanticSchema(2L, "root", Collections.singleton(semantics), new HashMap<FieldSemantics, SemanticField>());

        //when
        builder.addSemanticSchemas(Collections.singleton(schema));
        builder.addSemanticSchema(schema2);
        Localization local = builder.build();

        //then
        assertEquals(schema, local.getSemanticSchemas().get(1L));
        assertEquals(schema2, local.getSemanticSchemas().get(2L));
    }

    @Test
    public void shouldReturnConfiguration() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        HashMap<String, String> configuration = new HashMap<>();
        configuration.put("key", "value");

        //when
        builder.addConfiguration(configuration);
        Localization local = builder.build();

        //then
        assertEquals("value", local.getConfiguration("key"));
    }

    @Test
    public void shouldReturnDataFormat() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        HashMap<String, String> configuration = new HashMap<>();
        configuration.put("core.dataFormats", "1 ,2");

        //when
        builder.addConfiguration(configuration);
        Localization local = builder.build();

        //then
        assertEquals(Lists.newArrayList("1", "2"), local.getDataFormats());
    }

    @Test
    public void shouldReturnCulture() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        HashMap<String, String> configuration = new HashMap<>();
        configuration.put("core.culture", "culture");

        //when
        builder.addConfiguration(configuration);
        Localization local = builder.build();

        //then
        assertEquals("culture", local.getCulture());
    }

    @Test
    public void shouldReturnResources() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        HashMap<String, String> resources = new HashMap<>();
        resources.put("resourceName", "resourceString");

        //when
        builder.addResources(resources);
        Localization local = builder.build();

        //then
        assertEquals("resourceString", local.getResource("resourceName"));
    }

    @Test
    public void shouldReturnInclude() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.addInclude("id", "page");
        Localization local = builder.build();

        //then
        assertEquals(Lists.newArrayList("page"), local.getIncludes("id"));
    }

    @Test
    public void shouldReturnSiteLocalizations() throws InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        //given
        Builder builder = getBuilder();

        //when
        builder.addSiteLocalizations(Lists.newArrayList(new SiteLocalizationImpl() {
        }));
        Localization local = builder.build();

        //then
        assertEquals(1, local.getSiteLocalizations().size());
    }

    private Builder getBuilder() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<Builder> declaredConstructor = Builder.class.getDeclaredConstructor();
        declaredConstructor.setAccessible(true);
        Builder instance = declaredConstructor.newInstance();
        instance.setMediaRoot("/");

        return instance;
    }
}