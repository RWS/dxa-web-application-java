package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticProperty;

import java.util.ArrayList;
import java.util.List;

import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SCHEMA_ORG;
import static com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary.SDL_CORE;

/**
 * <p>ContentList class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@SemanticEntities({
        @SemanticEntity(entityName = "ItemList", vocabulary = SCHEMA_ORG, prefix = "s", public_ = true),
        @SemanticEntity(entityName = "ItemList", vocabulary = SDL_CORE, prefix = "i"),
        @SemanticEntity(entityName = "ContentQuery", vocabulary = SDL_CORE, prefix = "q")
})
public class ContentList extends AbstractEntityModel {

    @SemanticProperties({
            @SemanticProperty("s:headline"),
            @SemanticProperty("i:headline"),
            @SemanticProperty("q:headline")
    })
    @JsonProperty("Headline")
    private String headline;

    @SemanticProperties({
            @SemanticProperty("i:link"),
            @SemanticProperty("q:link")
    })
    @JsonProperty("Link")
    private Link link;

    @SemanticProperties({
            @SemanticProperty("i:pageSize"),
            @SemanticProperty("q:pageSize")
    })
    @JsonProperty("PageSize")
    private int pageSize;

    @SemanticProperties({
            @SemanticProperty("i:contentType"),
            @SemanticProperty("q:contentType")
    })
    @JsonProperty("ContentType")
    private Tag contentType;

    @SemanticProperties({
            @SemanticProperty("i:sort"),
            @SemanticProperty("q:sort")
    })
    @JsonProperty("Sort")
    private Tag sort;

    @JsonProperty("Start")
    private int start;

    @JsonProperty("CurrentPage")
    private int currentPage = 1;

    @JsonProperty("HasMore")
    private boolean hasMore;

    @SemanticProperties({
            @SemanticProperty("s:itemListElement"),
            @SemanticProperty("i:itemListElement")
    })
    @JsonProperty("ItemListElements")
    private List<Teaser> itemListElements = new ArrayList<>();

    /**
     * <p>Getter for the field <code>headline</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getHeadline() {
        return headline;
    }

    /**
     * <p>Setter for the field <code>headline</code>.</p>
     *
     * @param headline a {@link java.lang.String} object.
     */
    public void setHeadline(String headline) {
        this.headline = headline;
    }

    /**
     * <p>Getter for the field <code>link</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Link} object.
     */
    public Link getLink() {
        return link;
    }

    /**
     * <p>Setter for the field <code>link</code>.</p>
     *
     * @param link a {@link com.sdl.webapp.common.api.model.entity.Link} object.
     */
    public void setLink(Link link) {
        this.link = link;
    }

    /**
     * <p>Getter for the field <code>pageSize</code>.</p>
     *
     * @return a int.
     */
    public int getPageSize() {
        return pageSize;
    }

    /**
     * <p>Setter for the field <code>pageSize</code>.</p>
     *
     * @param pageSize a int.
     */
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    /**
     * <p>Getter for the field <code>contentType</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Tag} object.
     */
    public Tag getContentType() {
        return contentType;
    }

    /**
     * <p>Setter for the field <code>contentType</code>.</p>
     *
     * @param contentType a {@link com.sdl.webapp.common.api.model.entity.Tag} object.
     */
    public void setContentType(Tag contentType) {
        this.contentType = contentType;
    }

    /**
     * <p>Getter for the field <code>sort</code>.</p>
     *
     * @return a {@link com.sdl.webapp.common.api.model.entity.Tag} object.
     */
    public Tag getSort() {
        return sort;
    }

    /**
     * <p>Setter for the field <code>sort</code>.</p>
     *
     * @param sort a {@link com.sdl.webapp.common.api.model.entity.Tag} object.
     */
    public void setSort(Tag sort) {
        this.sort = sort;
    }

    /**
     * <p>Getter for the field <code>start</code>.</p>
     *
     * @return a int.
     */
    public int getStart() {
        return start;
    }

    /**
     * <p>Setter for the field <code>start</code>.</p>
     *
     * @param start a int.
     */
    public void setStart(int start) {
        this.start = start;
    }

    /**
     * <p>Getter for the field <code>currentPage</code>.</p>
     *
     * @return a int.
     */
    public int getCurrentPage() {
        return currentPage;
    }

    /**
     * <p>Setter for the field <code>currentPage</code>.</p>
     *
     * @param currentPage a int.
     */
    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    /**
     * <p>isHasMore.</p>
     *
     * @return a boolean.
     */
    public boolean isHasMore() {
        return hasMore;
    }

    /**
     * <p>Setter for the field <code>hasMore</code>.</p>
     *
     * @param hasMore a boolean.
     */
    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    /**
     * <p>Getter for the field <code>itemListElements</code>.</p>
     *
     * @return a {@link java.util.List} object.
     */
    public List<Teaser> getItemListElements() {
        return itemListElements;
    }

    /**
     * <p>Setter for the field <code>itemListElements</code>.</p>
     *
     * @param itemListElements a {@link java.util.List} object.
     */
    public void setItemListElements(List<Teaser> itemListElements) {
        this.itemListElements = itemListElements;
    }

    /**
     * {@inheritDoc}
     */
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
