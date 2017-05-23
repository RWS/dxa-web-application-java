package com.sdl.dxa.api.datamodel.model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.sdl.dxa.api.datamodel.DataModelSpringConfiguration;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.Iterator;
import java.util.TreeSet;
import java.util.stream.Stream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TaxonomyNodeModelDataJsonTest {

    private ObjectMapper objectMapper = new DataModelSpringConfiguration().dxaR2ObjectMapper();

    @Test
    public void shouldNameFieldCorrectly() throws IOException, ParseException {
        //given
        DateTime publishedDate = new DateTime();
        TreeSet<SitemapItemModelData> items = new TreeSet<>();

        SitemapItemModelData data = new TaxonomyNodeModelData()
                .setKey("Key")
                .setClassifiedItemsCount(42)
                .setDescription("Description")
                .setTaxonomyAbstract(true)
                .setWithChildren(true)
                .setTitle("Title")
                .setId("Id")
                .setUrl("Url")
                .setType("Type")
                .setItems(items)
                .setVisible(true)
                .setPublishedDate(publishedDate);

        //when
        String content = objectMapper.writeValueAsString(data);
        JsonNode jsonNode = objectMapper.readTree(content);

        //then

        Iterator<String> names = Stream.of("Key", "ClassifiedItemsCount", "Description", "IsAbstract", "HasChildNodes",
                "Title", "Id", "Url", "Type", "Items", "Visible", "PublishedDate").iterator();
        Iterator<Serializable> values = Stream.of("Key", 42, "Description", true, true,
                "Title", "Id", "Url", "Type", new TreeSet<>(), true, publishedDate).iterator();

        while (names.hasNext()) {
            String fieldName = names.next();
            assertTrue(jsonNode.has(fieldName));
            Object value = values.next().toString();
            JsonNode node = jsonNode.get(fieldName);

            Object actual = node.getNodeType() == JsonNodeType.ARRAY ? node.toString() : node.asText();
            if (fieldName.equals("PublishedDate")) {
                actual = ISODateTimeFormat.dateTimeParser().parseDateTime(actual.toString());
            }

            assertEquals(value, actual.toString());
        }
        assertFalse(jsonNode.has("OriginalTitle"));
    }
}