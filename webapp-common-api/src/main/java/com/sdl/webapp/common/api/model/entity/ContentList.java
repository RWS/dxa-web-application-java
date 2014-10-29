package com.sdl.webapp.common.api.model.entity;

import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true)
public class ContentList<T> extends AbstractEntity {

    @SemanticProperty("s:headline")
    private String headline;

    private EmbeddedLink link;

    private int pageSize;

    private Tag contentType;

    private Tag sort;

    private int start;

    private int currentPage = 1;

    private boolean hasMore;

    @SemanticProperty("s:itemListElement")
    private List<T> itemListElements = new ArrayList<>();

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

    public List<T> getItemListElements() {
        return itemListElements;
    }

    public void setItemListElements(List<T> itemListElements) {
        this.itemListElements = itemListElements;
    }
}
