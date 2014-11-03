package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SDL_CORE;

@SemanticEntities({
        @SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
        @SemanticEntity(entityName = "ContentQuery", vocabulary = SDL_CORE, prefix = "q")
})

public class ContentList extends AbstractEntity {

    @SemanticProperties({
            @SemanticProperty("s:headline"),
            @SemanticProperty("q:headline")
    })
    private String headline;

    @SemanticProperty("q:link")
    private EmbeddedLink link;

    @SemanticProperty("q:pageSize")
    private int pageSize;

    @SemanticProperty("q:contentType")
    private Tag contentType;

    @SemanticProperty("q:sort")
    private Tag sort;

    private int start;

    private int currentPage = 1;

    private boolean hasMore;

    @SemanticProperty("s:itemListElement")
    private List<Teaser> itemListElements = new ArrayList<>();

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public EmbeddedLink getLink() {
        return link;
    }

    public void setLink(EmbeddedLink link) {
        this.link = link;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Tag getContentType() {
        return contentType;
    }

    public void setContentType(Tag contentType) {
        this.contentType = contentType;
    }

    public Tag getSort() {
        return sort;
    }

    public void setSort(Tag sort) {
        this.sort = sort;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<Teaser> getItemListElements() {
        return itemListElements;
    }

    public void setItemListElements(List<Teaser> itemListElements) {
        this.itemListElements = itemListElements;
    }

    @Override
    public String toString() {
        return "ContentList{" +
                "headline='" + headline + '\'' +
                ", link=" + link +
                ", pageSize=" + pageSize +
                ", contentType=" + contentType +
                ", sort=" + sort +
                ", start=" + start +
                ", currentPage=" + currentPage +
                ", hasMore=" + hasMore +
                ", itemListElements=" + itemListElements +
                '}';
    }
}
