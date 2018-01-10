package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
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

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import static org.apache.commons.lang3.StringUtils.isEmpty;

/**
 * @dxa.publicApi
 */
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
    @JsonDeserialize(as = LinkedHashSet.class)
    private Set<SitemapItem> items;

    @JsonProperty("PublishedDate")
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
    public Set<SitemapItem> getItems() {
        return this.items == null ? Collections.emptySet() : this.items;
    }

    /**
     * Setter for the children items which also sets parent field to the current object.
     * <p></p>
     * <strong>NB! the given set may be wrapped with another {@link Set} implementation!</strong>
     * This means that the current code may not work as you expect:
     * <pre><code>
     * SitemapItem item = new SitemapItem();
     * Set&lt;SitemapItem&gt; children = new HashSet&lt;&gt;();
     * item.setItems(children);
     * children.add(new SitemapItem()); // here children may be a different set than one that might be really set!
     *
     * // so this MAY BE true:
     * assert children != item.getItems();
     *
     * // although this still may be true:
     * assert children.equals(item.getItems());
     * </code></pre>
     * Out of the box for DXA it is true for {@link TaxonomyNode} as it wraps the passed collection with a {@link java.util.SortedSet}.
     * Use {@link #addItem(SitemapItem)} to add items.
     *
     * @param items items to set
     */
    public void setItems(@Nullable Set<SitemapItem> items) {
        this.items = wrapItems(items);
        ensureParentsSet(items);
    }

    private void ensureParentsSet(@Nullable Collection<SitemapItem> items) {
        if (items != null) {
            for (SitemapItem item : items) {
                if (item != null) {
                    item.parent = this;
                }
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
            this.items = wrapItems(new LinkedHashSet<>());
        }
        item.setParent(this);
        this.items.add(item);
        return this;
    }

    /**
     * Removes an item from a collection of items.
     *
     * @param item item to remove
     * @return true is item was removed, false if there was no item or collection wasn't initialized
     */
    public boolean removeItem(SitemapItem item) {
        return this.items != null && this.items.remove(item);
    }

    @Contract("null -> !null; !null -> !null")
    protected Set<SitemapItem> wrapItems(@Nullable Set<SitemapItem> items) {
        return items == null ? new LinkedHashSet<>() : items;
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
        link.setId(getId());
        link.setUrl(isEmpty(getUrl()) ? getUrl() : linkResolver.resolveLink(getUrl(), localization.getId()));
        link.setLinkText(getTitle());
        return link;
    }
}
