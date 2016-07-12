package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.localization.Localization;
import org.joda.time.DateTime;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class ArticleTest {

    private static Article article = new Article();
    private static DateTime time = DateTime.now();
    private static List<Paragraph> paragraphList = new ArrayList<>();
    private static Map<String, Object> xpmMetadata = new HashMap<>();
    private static Map<String, String> xpmPropertyMetadata = new HashMap<>();

    private static Image image = mock(Image.class);
    private static Paragraph paragraph = mock(Paragraph.class);

    static {
        paragraphList.add(paragraph);
        xpmMetadata.put("key", "value");
        xpmPropertyMetadata.put("key1", "value1");

        article.setId("ArticleId");
        article.setHeadline("ArticleHeadline");
        article.setDescription("ArticleDescription");
        article.setDate(time);
        article.setImage(image);
        article.setArticleBody(paragraphList);
        article.setXpmMetadata(xpmMetadata);
        article.setXpmPropertyMetadata(xpmPropertyMetadata);
    }

    @Test
    public void shouldReturnTitle() {
        //given

        //when

        //then
        assertEquals("ArticleId", article.getId());
    }

    @Test
    public void shouldReturnHeadline() {
        //given

        //when

        //then
        assertEquals("ArticleHeadline", article.getHeadline());
    }

    @Test
    public void shouldReturnDescription() {
        //given

        //when

        //then
        assertEquals("ArticleDescription", article.getDescription());
    }

    @Test
    public void shouldReturnDate() {
        //given

        //when

        //then
        assertEquals(time, article.getDate());
    }

    @Test
    public void shouldReturnImage() {
        //given

        //when

        //then
        assertEquals(image, article.getImage());
    }

    @Test
    public void shouldReturnArticleBody() {
        //given

        //when

        //then
        assertEquals(paragraphList, article.getArticleBody());
    }

    @Test
    public void shouldReturnXpmMarkup() {
        //given
        Localization localization = mock(Localization.class);

        //when

        //then
        assertEquals("<!-- Start Component Presentation: {\r\n" +
                "  \"key\" : \"value\"\r\n" +
                "} -->", article.getXpmMarkup(localization));
    }

    @Test
    public void shouldReturnXpmMetadata() {
        //given

        //when

        //then
        assertEquals(xpmMetadata, article.getXpmMetadata());
    }

    @Test
    public void shouldReturnXpmPropertyMarkup() {
        //given

        //when

        //then
        assertEquals(xpmPropertyMetadata, article.getXpmPropertyMetadata());
    }
}