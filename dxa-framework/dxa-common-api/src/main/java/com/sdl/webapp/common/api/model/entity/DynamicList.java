package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.query.AbstractQuery;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.jetbrains.annotations.Contract;

import java.util.List;

/**
 * Abstract dynamic list contains basic API methods for all lists for Broker API requests.
 *
 * @param <T> resulting type of {@link EntityModel} that is returned by implementor
 * @param <Q> type of a query
 */
@Data
@EqualsAndHashCode(callSuper = true)
public abstract class DynamicList<T extends EntityModel, Q extends AbstractQuery> extends AbstractEntityModel {

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
    @JsonProperty("QueryResults")
    public abstract List<T> getQueryResults();

    /**
     * Sets query results, populating all data fields according to implementation logic.
     *
     * @param queryResults results of query
     * @param hasMore      if there are more results but they are not in a list
     */
    public abstract void setQueryResults(List<T> queryResults, boolean hasMore);

    /**
     * Constructs a {@link EntityModel} from {@link ComponentMetadata}.
     *
     * @param componentMetadata component metadata to be used for entity construction
     * @return entity of type {@link T} from metadata, or <code>null</code> for <code>null</code> input
     */
    @Contract("null -> null; !null -> !null")
    public abstract T getEntity(ComponentMetadata componentMetadata);
}
