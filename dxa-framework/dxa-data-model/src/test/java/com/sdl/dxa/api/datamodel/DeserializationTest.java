package com.sdl.dxa.api.datamodel;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.sdl.dxa.api.datamodel.model.BinaryContentData;
import com.sdl.dxa.api.datamodel.model.ContentModelData;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.ExternalContentData;
import com.sdl.dxa.api.datamodel.model.KeywordModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.RegionModelData;
import com.sdl.dxa.api.datamodel.model.RichTextData;
import com.sdl.dxa.api.datamodel.model.known.FirstChildKnownClass;
import com.sdl.dxa.api.datamodel.model.known.KnownClass;
import com.sdl.dxa.api.datamodel.model.known.SecondChildKnownClass;
import com.sdl.dxa.api.datamodel.model.unknown.UnknownModelData;
import com.sdl.dxa.api.datamodel.model.util.ListWrapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import static org.hamcrest.collection.IsMapContaining.hasEntry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = DeserializationTest.SpringConfigurationContext.class)
public class DeserializationTest {

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldDeserializePageModel() throws IOException {
        //given
        File file = new ClassPathResource("dxa20json/pageModel.json").getFile();

        //when
        PageModelData page = objectMapper.readValue(file, PageModelData.class);

        //then
        assertEquals("640", page.getId());
        assertEquals("Home", page.getTitle());
        assertEquals("/autotest-r2/index", page.getUrlPath());

        assertThat(page.getMeta(), hasEntry("twitter:card", "summary"));
        assertThat(page.getMeta(), hasEntry("og:title", "Home"));
        assertThat(page.getMeta(), hasEntry("og:type", "article"));
        assertThat(page.getMeta(), hasEntry("description", "Description"));

        assertEquals("ViewName", page.getMvcData().getViewName());
        assertEquals("ControllerName", page.getMvcData().getControllerName());
        assertEquals("ControllerAreaName", page.getMvcData().getControllerAreaName());
        assertEquals("AreaName", page.getMvcData().getAreaName());
        assertEquals("ActionName", page.getMvcData().getActionName());
        assertEquals("Value1", page.getMvcData().getParameters().get("Key1"));

        assertExtensionData(page);

        assertRegions(page.getRegions());
    }

    private void assertExtensionData(PageModelData page) throws IOException {
        Map<String, Object> extensionData = page.getExtensionData();
        _assertExtensionData(extensionData);

        // what if we serialize and deserialize? do we still have type info for unknown classes?
        String serialized1 = objectMapper.writeValueAsString(page);
        _assertExtensionData(objectMapper.readValue(serialized1, PageModelData.class).getExtensionData());

        // and again to confirm that deserializing of previously serialized content results in the same
        String serialized2 = objectMapper.writeValueAsString(page);
        _assertExtensionData(objectMapper.readValue(serialized2, PageModelData.class).getExtensionData());

        // and even content is the same
        assertEquals(serialized1, serialized2);
    }

    private void _assertExtensionData(Map<String, Object> extensionData) throws IOException {
        assertTrue(extensionData.get("EntityModelData") instanceof EntityModelData);

        assertTrue(extensionData.get("EntityModelDatas") instanceof ListWrapper);
        assertTrue(((ListWrapper) extensionData.get("EntityModelDatas")).get(0) instanceof EntityModelData);

        assertTrue(extensionData.get("KnownClass") instanceof KnownClass);

        assertTrue(extensionData.get("KnownClasses") instanceof ListWrapper);
        assertTrue(((ListWrapper) extensionData.get("KnownClasses")).get(0) instanceof KnownClass);

        assertTrue(extensionData.get("KnownParentClasses") instanceof ListWrapper);
        assertTrue(((ListWrapper) extensionData.get("KnownParentClasses")).get(0) instanceof FirstChildKnownClass);
        assertTrue(((ListWrapper) extensionData.get("KnownParentClasses")).get(1) instanceof SecondChildKnownClass);

        // ===

        assertUnknown(extensionData, "UnknownClassNoType");
        assertUnknown(extensionData, "UnknownClass");
        assertUnknown(extensionData, "UnknownClasses");
        assertUnknown(extensionData, "UnknownParentClasses");
        assertUnknown(extensionData, "DeserializerTest");
    }

    private void assertUnknown(Map<String, Object> extensionData, String fieldName) throws IOException {
        ClassPathResource resource = new ClassPathResource("dxa20json/unknown/" + fieldName);
        String content = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        assertTrue(extensionData.get(fieldName) instanceof UnknownModelData);
        assertEquals(content, ((UnknownModelData) extensionData.get(fieldName)).getContent());
    }

    private void assertRegions(List<RegionModelData> regions) {
        ListIterator<RegionModelData> iterator = regions.listIterator();
        RegionModelData region = iterator.next();
        assertEquals("Header", region.getName());
        assertEquals("1234", region.getIncludePageId());
        assertEquals("Header", region.getMvcData().getViewName());

        assertTrue(region.getRegions().size() == 2);
        assertEquals("IncludeRegion1", region.getRegions().get(0).getName());
        assertEquals("IncludeRegion2", region.getRegions().get(1).getName());

        assertEntities(region);

        region = iterator.next();
        assertEquals("Footer", region.getName());
        assertEquals("1235", region.getIncludePageId());
        assertEquals("Footer", region.getMvcData().getViewName());

        assertFalse(iterator.hasNext());
    }

    private void assertEntities(RegionModelData region) {
        assertTrue(region.getEntities().size() == 2);
        EntityModelData entity = region.getEntities().get(0);

        assertEquals("1024", entity.getId());
        assertEquals("linkUrl", entity.getLinkUrl());
        assertEquals("ViewName", entity.getMvcData().getViewName());
        assertEquals("10029", entity.getSchemaId());
        assertEquals("class1 class2", entity.getHtmlClasses());

        assertEquals("fileName", entity.getBinaryContent().getFileName());
        assertEquals(Long.MAX_VALUE, entity.getBinaryContent().getFileSize());
        assertEquals("application/xml", entity.getBinaryContent().getMimeType());
        assertEquals("url", entity.getBinaryContent().getUrl());

        assertEquals("displayTypeId", entity.getExternalContent().getDisplayTypeId());
        assertEquals("id", entity.getExternalContent().getId());
        assertEquals("Value1", entity.getExternalContent().getMetadata().get("Key1"));

        assertEquals("Heading", entity.getContent().get("Heading"));
        assertEquals("Ending", entity.getContent().get("Ending"));

        //noinspection unchecked
        ListWrapper<String> strings = (ListWrapper<String>) entity.getContent().get("Strings");
        assertEquals("string_1", strings.get(0));
        assertEquals("string_2", strings.get(1));

        //noinspection unchecked
        ListWrapper<Float> floats = (ListWrapper<Float>) entity.getContent().get("Floats");
        assertEquals(666.666f, floats.get(0), 0.0f);
        assertEquals(42f, floats.get(1), 0.0f);

        String dateTimeStr = "1970-12-16T11:34:56.000+0000";
        //noinspection unchecked
        ListWrapper<String> dates = (ListWrapper<String>) entity.getContent().get("Dates");
        assertEquals(dateTimeStr, dates.get(0));

        //noinspection unchecked
        ListWrapper<String> dateTimes = (ListWrapper<String>) entity.getContent().get("DateTimes");
        assertEquals(dateTimeStr, dateTimes.get(0));

        assertTrue(entity.getContent().get("itemListElement") instanceof ListWrapper.ContentModelDataListWrapper);
        List<ContentModelData> cmds = ((ListWrapper.ContentModelDataListWrapper) entity.getContent().get("itemListElement")).getValues();
        assertEquals("subheading", cmds.get(0).get("subheading"));

        assertTrue(cmds.get(0).get("media") instanceof EntityModelData);
        EntityModelData media = cmds.get(0).getAndCast("media", EntityModelData.class);
        assertEquals("755", media.getId());
        assertEquals("79", media.getSchemaId());
        assertEquals("755-url", media.getBinaryContent().getUrl());

        ContentModelData link = cmds.get(0).getAndCast("link", ContentModelData.class);
        assertEquals("linkText", link.getAndCast("linkText", String.class));
        assertEquals("fragment", link.getAndCast("content", RichTextData.class).getFragments().get(0));
        assertTrue(link.getAndCast("content", RichTextData.class).getFragments().size() == 1);

        assertEquals("subheading2", cmds.get(1).get("subheading"));
        ListWrapper.RichTextDataListWrapper rtd = cmds.get(1).getAndCast("content", ListWrapper.RichTextDataListWrapper.class);
        assertEquals("fragment2", rtd.get(0).getFragments().get(0));
        assertEquals("756", rtd.get(0).getAndCast(1, EntityModelData.class).getId());

        assertTrue(cmds.size() == 2);
        assertTrue(entity.getContent().size() == 7);

        assertEquals("XpmValue1", entity.getXpmMetadata().get("XpmKey1"));
        assertEquals("XpmValue2", entity.getXpmMetadata().get("XpmKey2"));

        assertEquals("Value1", entity.getMetadata().get("Key1"));
        assertEquals("Value2", entity.getMetadata().get("Key2"));

        assertEquals("ExtensionValue1", entity.getExtensionData().get("ExtensionKey1"));
        assertEquals(1, entity.getExtensionData().get("ExtensionKey2"));

        assertEquals("1025", region.getEntities().get(1).getId());
    }

    @Test
    public void shouldSerialize_AndDeserialize_TheSame() throws IOException {
        //when
        ContentModelData rootCmd = new ContentModelData();
        rootCmd.put("key", "value");
        rootCmd.put("binary", new BinaryContentData("filename", 1L, "mimeType", "url"));

        ContentModelData innerCmd = new ContentModelData();
        innerCmd.put("key_inner", "value_inner");

        ContentModelData x2InnerCmd = new ContentModelData();
        x2InnerCmd.put("key_inner_inner", "val_inner_inner");
        x2InnerCmd.put("key_inner_inner_2", "val_inner_inner");
        innerCmd.put("cmd_inner", x2InnerCmd);

        ListWrapper listStrs = new ListWrapper<>(Lists.newArrayList("str1", "str2", "str3"));
        x2InnerCmd.put("listStrs", listStrs);

        ContentModelData cmdInList = new ContentModelData();
        cmdInList.put("key1", "value1");
        x2InnerCmd.put("listCmd", new ListWrapper.ContentModelDataListWrapper(Lists.newArrayList(cmdInList, cmdInList)));

        x2InnerCmd.put("listKmd", new ListWrapper.KeywordModelDataListWrapper(
                Lists.newArrayList(new KeywordModelData("id", "desc", "key", "taxId", "title"))));
        rootCmd.put("cmd", innerCmd);

        Date date = new Date();
        ExternalContentData ecd = new ExternalContentData("displayTypeId", "id", "", rootCmd);
        DeserializeTrip trip = new DeserializeTrip(ecd, date, new DeserializeTrip(ecd, null, null, null), null);

        //when
        String serialized = objectMapper.writeValueAsString(trip);
        DeserializeTrip deserialized = objectMapper.readValue(serialized, DeserializeTrip.class);
        // After the first serialize, we might've added $type because of polymorphic mapping, but later we save if in CMDs and restore it from there
        // this leads to reordering of the properties, and serialized content is not exactly the same while is semantically equal.
        // To fix this we compare only 2 & 3 attempt of serialization but compare every result of deserialization.

        String serialized2 = objectMapper.writeValueAsString(deserialized);
        DeserializeTrip deserialized2 = objectMapper.readValue(serialized2, DeserializeTrip.class);
        String serialized3 = objectMapper.writeValueAsString(deserialized);
        DeserializeTrip deserialized3 = objectMapper.readValue(serialized2, DeserializeTrip.class);

        //then
        assertEquals(serialized2, serialized3);
        assertEquals(deserialized, deserialized2);
        assertEquals(deserialized, deserialized3);
    }

    @Data
    @AllArgsConstructor
    @SuppressWarnings("DefaultAnnotationParam")
    @JsonIgnoreProperties(ignoreUnknown = false)
    //particularly this class should not tolerate unknown properties (check that $type is no in JSON)
    private static class DeserializeTrip {

        public ExternalContentData externalContentData;

        public Date date;

        public DeserializeTrip deserializeTrip;

        public Object nullField;
    }

    @Configuration
    public static class SpringConfigurationContext {

        @Bean
        public ObjectMapper objectMapper() {
            return new DataModelSpringConfiguration().dxaR2ObjectMapper();
        }
    }
}
