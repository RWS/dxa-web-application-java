package com.sdl.webapp.common.api.model.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class SimpleBrokerQuery extends AbstractQuery {

    private int publicationId;

    private int schemaId;

    private String path;

    private String sort;

    private String cursor;

    private Multimap<String, String> keywordFilters = ArrayListMultimap.create();
}
