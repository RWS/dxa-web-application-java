package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Data
@ToString(exclude = {"parent"})
@EqualsAndHashCode(callSuper = true, of = {"title", "originalTitle", "type", "publishedDate"})
@Slf4j
@NoArgsConstructor
public class SitemapItem extends AbstractEntityModel {

    @JsonProperty("Title")
    private String title;

    @JsonProperty("Url")
    private String url;

    @JsonProperty("Type")
    private String type;

    @JsonProperty("Items")
    private List<SitemapItem> items;

    @JsonProperty("PublishedDate")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private DateTime publishedDate;

    @JsonProperty("Visible")
    private boolean visible;

    @JsonIgnore
    private SitemapItem parent;

    @JsonIgnore
    private String originalTitle;

    public SitemapItem(SitemapItem other) {
        super(other);
        this.title = other.title;
        this.url = other.url;
        this.type = other.type;
        this.items = other.items;
        this.publishedDate = other.publishedDate;
        this.visible = other.visible;
        this.parent = other.parent;
        this.originalTitle = other.originalTitle;
    }

    @NotNull
    public List<SitemapItem> getItems() {
        return this.items == null ? Collections.<SitemapItem>emptyList() : this.items;
    }

    /**
     * Setter for the children items which also sets parent field to the current object.
     *
     * @param items items to set
     */
    public void setItems(@Nullable List<SitemapItem> items) {
        this.items = items;
        if (items != null) {
            for (SitemapItem item : items) {
                item.parent = this;
            }
        }
    }

    /**
     * Adds an item to a collection of items and initializes it if needed.
     *
     * @param item item to add
     * @return itself
     */
    @NotNull
    public SitemapItem addItem(SitemapItem item) {
        if (this.items == null) {
            this.items = new ArrayList<>();
        }
        this.items.add(item);
        return this;
    }

    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for title which also sets original title <strong>if original title is not yet set</strong>.
     *
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = title;
        if (this.originalTitle == null) {
            setOriginalTitle(title);
        }
    }

    /**
     * Creates a {@link Link} from the current object. Never returns null.
     *
     * @param linkResolver link resolver to use
     * @param localization current localization
     * @return a constructed {@link Link} object, never null
     */
    @Contract("_, _ -> !null")
    public Link createLink(@NonNull LinkResolver linkResolver, @NonNull Localization localization) {
        Link link = new Link();
        link.setUrl(isEmpty(getUrl()) ? getUrl() : linkResolver.resolveLink(getUrl(), localization.getId()));
        link.setLinkText(getTitle());
        return link;
    }

    /**
     * Finds a SitemapItem with a given URL path in the Navigation subtree rooted by this {@link SitemapItem}.
     *
     * @param urlToFind The URL path to search for
     * @return a {@link SitemapItem} with the given URL path or <code>null</code> if no such item is found
     */
    @Nullable
    public SitemapItem findWithUrl(@NonNull String urlToFind) {
        if (getUrl() != null && urlToFind.matches(getUrl() + "/?")) {
            return this;
        }

        for (SitemapItem item : getItems()) {
            SitemapItem sub = item.findWithUrl(urlToFind);
            if (sub != null) {
                return sub;
            }
        }
        return null;
    }
}
