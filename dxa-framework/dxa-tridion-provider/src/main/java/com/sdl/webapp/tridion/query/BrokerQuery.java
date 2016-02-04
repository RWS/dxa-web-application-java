package com.sdl.webapp.tridion.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sdl.webapp.common.api.model.entity.Teaser;
import lombok.Data;

import java.util.List;

@Data
/**
 * <p>Abstract BrokerQuery class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
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
