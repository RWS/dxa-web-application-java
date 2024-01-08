package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.FluentIterable;
import com.sdl.dxa.caching.NoOutputCache;
import com.sdl.webapp.common.api.formatters.support.FeedItem;
import com.sdl.webapp.common.api.formatters.support.FeedItemsProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.query.AbstractQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Contract;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract dynamic list contains basic API methods for all lists for Broker API requests.
 *
 * @param <T> resulting type of {@link EntityModel} that is returned by implementor
 * @param <Q> type of a query
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoOutputCache
public abstract class DynamicList<T extends EntityModel, Q extends AbstractQuery> extends AbstractEntityModel implements FeedItemsProvider {

    @JsonProperty("Start")
    private int start;

    /**
     * Returns a {@link Q} object that represents the underlying query.
     *
     * @param localization current localization
     * @return a query object, never null
     */
    @Contract("null -> fail; !null -> !null")
    public abstract Q getQuery(Localization localization);

    /**
     * Returns list of query results.
     *
     * @return a list of {@link T} as a query result
     */
    @JsonIgnore
    public abstract List<T> getQueryResults();

    /**
     * Sets query results, populating all data fields according to implementation logic.
     *
     * @param queryResults results of query
     * @param hasMore      if there are more results but they are not in a list
     */
    public abstract void setQueryResults(List<T> queryResults, boolean hasMore);

    /**
     * Sets query results, populating all data fields according to implementation logic.
     *
     * @param queryResults results of query
     */
    public abstract void setQueryResults(List<T> queryResults);

    /**
     * Returns a class of {@link EntityModel} that the current implementation of {@link DynamicList} works with.
     *
     * @return a class object of T, should never return null
     */
    public abstract Class<T> getEntityType();

    @Override
    public List<FeedItem> extractFeedItems() {
        return collectFeedItems(FluentIterable.from(getQueryResults()).filter(FeedItemsProvider.class).toList());
    }

    @Override
    public DynamicList deepCopy() {
        DynamicList clone = (DynamicList) super.deepCopy();
        if (getQueryResults() != null) {
            clone.setQueryResults(new ArrayList<EntityModel>(getQueryResults()));
        }
        return clone;
    }
}
