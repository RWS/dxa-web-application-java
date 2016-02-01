package com.sdl.webapp.tridion.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sdl.webapp.common.api.model.entity.Teaser;
import lombok.Data;

import java.util.List;

@Data
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

    public abstract List<Teaser> executeQuery() throws BrokerQueryException;
}
