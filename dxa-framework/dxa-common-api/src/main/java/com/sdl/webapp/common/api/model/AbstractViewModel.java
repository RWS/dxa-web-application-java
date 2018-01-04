package com.sdl.webapp.common.api.model;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;
import com.sdl.webapp.common.api.model.mvcdata.MvcDataImpl;
import com.sdl.webapp.common.api.serialization.json.DxaViewModelJsonChainFilter;
import com.sdl.webapp.common.api.serialization.json.annotation.JsonXpmAware;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Basic abstract implementation of the {@link ViewModel}. Default extending point for your custom {@link PageModel}.
 *
 * @dxa.publicApi
 */
@Data
@NoArgsConstructor
@Slf4j
@JsonFilter(DxaViewModelJsonChainFilter.FILTER_NAME)
public abstract class AbstractViewModel implements ViewModel {

    @JsonProperty("ExtensionData")
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private Map<String, Object> extensionData;

    @JsonIgnore
    private String htmlClasses;

    @JsonXpmAware
    @JsonProperty("XpmMetadata")
    private Map<String, Object> xpmMetadata = new HashMap<>();

    @JsonProperty("MvcData")
    private MvcData mvcData;

    public AbstractViewModel(ViewModel other) {
        this.extensionData = other.getExtensionData();
        this.htmlClasses = other.getHtmlClasses();
        if (other.getXpmMetadata() != null) {
            this.xpmMetadata.putAll(other.getXpmMetadata());
        }
        this.mvcData = other.getMvcData() != null ? new MvcDataImpl(other.getMvcData()) : null;
    }

    @Override
    public void addExtensionData(String key, Object value) {
        if (extensionData == null) {
            extensionData = new HashMap<>();
        }
        extensionData.put(key, value);
    }

    public void setXpmMetadata(@NonNull Map<String, Object> xpmMetadata) {
        this.xpmMetadata = xpmMetadata;
    }

    public void addXpmMetadata(@NonNull Map<String, Object> xpmMetadata) {
        this.xpmMetadata.putAll(xpmMetadata);
    }

    protected List<FeedItem> collectFeedItems(Collection<? extends FeedItemsProvider> feedItemsProviders) {
        List<FeedItem> feedItems = new ArrayList<>();
        if (isEmpty(feedItemsProviders)) {
            log.trace("FeedItemsProvider collection is empty, return empty list");
            return feedItems;
        }

        for (FeedItemsProvider provider : feedItemsProviders) {
            feedItems.addAll(provider.extractFeedItems());
        }
        return feedItems;
    }
}
