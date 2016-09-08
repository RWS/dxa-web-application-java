package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.util.LocalizationUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;

import java.util.List;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Data
@ToString(exclude = {"parent"})
@EqualsAndHashCode(callSuper = true)
@Slf4j
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

    public String getTitle() {
        return this.title;
    }

    /**
     * Setter for the title which also removes sequence (001 Home -&gt; Home) combination from the title.
     *
     * @param title title to set
     */
    public void setTitle(String title) {
        this.title = LocalizationUtils.removeSequenceFromPageTitle(title);
    }

    public List<SitemapItem> getItems() {
        return this.items;
    }

    /**
     * Setter for the children items which also sets parent field to the current objet.
     *
     * @param items items to set
     */
    public void setItems(List<SitemapItem> items) {
        this.items = items;
        for (SitemapItem item : items) {
            item.parent = this;
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
        if (getUrl() != null && getUrl().equalsIgnoreCase(urlToFind)) {
            return this;
        }

        if (getItems() == null) {
            return null;
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
