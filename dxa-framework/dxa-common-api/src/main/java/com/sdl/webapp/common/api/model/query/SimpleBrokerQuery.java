package com.sdl.webapp.common.api.model.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SimpleBrokerQuery extends AbstractQuery {

    private int publicationId;

    private int schemaId;

    private String sort;

    private Multimap<String, String> keywordFilters = ArrayListMultimap.create();
}
