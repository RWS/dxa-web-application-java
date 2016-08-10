package com.sdl.webapp.common.api.model.query;

import lombok.Builder;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Date;
import java.util.Map;

@Value
@Builder
@Slf4j
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

    private Map<String, Object> custom;

    /**
     * Returns a value of a component's field <code>fieldName</code> if exists casting it to a given type.
     *
     * @param fieldName name of the field to look for
     * @param ofType    expected type of that field
     * @param <T>       generic expected type
     * @return value casted to a given type, or null if not found or impossible to cast
     */
    @Nullable
    public <T> T getCustom(String fieldName, @NotNull Class<T> ofType) {
        if (custom != null && custom.containsKey(fieldName)) {
            Object value = custom.get(fieldName);
            if (ofType.isAssignableFrom(value.getClass())) {
                log.trace("Returning field {} with value {} of type {}", fieldName, value, ofType);
                return ofType.cast(value);
            }
            log.warn("Field {} was found of class {} while expected it to be {}", fieldName, value.getClass(), ofType);
        }
        return null;
    }
}
