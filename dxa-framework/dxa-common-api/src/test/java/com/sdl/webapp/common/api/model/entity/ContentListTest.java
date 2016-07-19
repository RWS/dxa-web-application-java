package com.sdl.webapp.common.api.model.entity;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class ContentListTest {
    private static ContentList ContentList = new ContentList();

    private static Link link = mock(Link.class);
    private static Tag tag = mock(Tag.class);
    private static List<Teaser> elements = new ArrayList<>();

    static {
        ContentList.setHeadline("ContentListHeadLine");
        ContentList.setLink(link);
        ContentList.setPageSize(100);
        ContentList.setContentType(tag);
        ContentList.setSort(tag);
        ContentList.setStart(1);
        ContentList.setCurrentPage(2);
        ContentList.setHasMore(true);
        ContentList.setItemListElements(elements);
    }

    @Test
    public void shouldReturnHeadLine() {
        //given

        //when

        //then
        assertEquals("ContentListHeadLine", ContentList.getHeadline());
    }

    @Test
    public void shouldReturnLink() {
        //given

        //when

        //then
        assertEquals(link, ContentList.getLink());
    }

    @Test
    public void shouldReturnPageSize() {
        //given

        //when

        //then
        assertEquals(100, ContentList.getPageSize());
    }

    @Test
    public void shouldReturnStart() {
        //given

        //when

        //then
        assertEquals(1, ContentList.getStart());
    }

    @Test
    public void shouldReturnCurrentPage() {
        //given

        //when

        //then
        assertEquals(2, ContentList.getCurrentPage());
    }

    @Test
    public void shouldReturnHasMore() {
        //given

        //when

        //then
        assertEquals(true, ContentList.isHasMore());
    }

    @Test
    public void shouldReturnSort() {
        //given

        //when

        //then
        assertEquals(tag, ContentList.getSort());
    }

    @Test
    public void shouldReturnContentType() {
        //given

        //when

        //then
        assertEquals(tag, ContentList.getContentType());
    }

    @Test
    public void shouldReturnItemListElements() {
        //given

        //when

        //then
        assertEquals(elements, ContentList.getItemListElements());
    }

    @Test
    public void shouldReturnToString() {
        //given

        //when

        //then
        assertEquals("ContentList(" +
                     "headline=" + "ContentListHeadLine" +
                     ", link=" + link.toString() +
                     ", pageSize=" + 100 +
                     ", contentType=" + tag.toString() +
                     ", sort=" + tag.toString() +
                     ", start=" + 1 +
                     ", currentPage=" + 2 +
                     ", hasMore=" + true +
                     ", itemListElements=" + elements.toString() +
                     ")",

                ContentList.toString());
    }

}