package com.sdl.tridion.referenceimpl.common.model.entity;

import java.util.ArrayList;
import java.util.List;

public class ContentList<T> extends EntityBase {

    private String headline;
    private Link link;
    private int pageSize;
    private Tag contentType;
    private Tag sort;
    private int start;
    private int currentPage = 1;
    private boolean hasMore;
    private List<T> itemListElements = new ArrayList<>();

    public String getHeadline() {
        return headline;
    }

    public void setHeadline(String headline) {
        this.headline = headline;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
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
