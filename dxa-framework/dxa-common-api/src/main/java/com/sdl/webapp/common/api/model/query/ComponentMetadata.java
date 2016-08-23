package com.sdl.webapp.common.api.model.query;

import lombok.Builder;
import lombok.Value;

import java.util.Date;
import java.util.Map;

/**
 * Component metadata DTO, structure is aligned with CD broker API metadata.
 */
@Value
@Builder
public class ComponentMetadata {

    private String id;

    private String componentUrl;

    private String publicationId;

    private String owningPublicationId;

    private String schemaId;

    private String title;

    private Date modificationDate;

    private Date initialPublicationDate;

    private Date lastPublicationDate;

    private Date creationDate;

    private String author;

    private boolean multimedia;

    private Map<String, MetaEntry> custom;

    /**
     * Type of the meta entry.
     */
    public enum MetaType {
        DATE, FLOAT, STRING
    }

    @Value
    @Builder
    public static class MetaEntry {

        private Object value;

        private MetaType metaType;
    }
}
