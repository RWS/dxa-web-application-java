package com.sdl.webapp.tridion.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sdl.dxa.modules.core.model.entity.Teaser;
import lombok.Data;

import java.util.List;

@Data
/**
 * <p>Abstract BrokerQuery class.</p>
 */
public abstract class BrokerQuery {
    private int schemaId;
    private int publicationId;
    private int maxResults;
    private String sort;
    private int start;
    private int pageSize;
    private Multimap<String, String> keywordFilters = ArrayListMultimap.create();
    private boolean hasMore;

    // TODO: Add filters for custom meta data

    /**
     * <p>executeQuery.</p>
     *
     * @return a {@link java.util.List} object.
     * @throws com.sdl.webapp.tridion.query.BrokerQueryException if any.
     */
    public abstract List<Teaser> executeQuery() throws BrokerQueryException;
}
