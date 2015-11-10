package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sdl.webapp.common.api.localization.Localization;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

public final class SemanticSchema {

    private final long id;

    private final String rootElement;

    private final Set<EntitySemantics> entitySemantics;

    private final Map<FieldSemantics, SemanticField> semanticFields;
    private Localization localization;

    public SemanticSchema(long id, String rootElement, Set<EntitySemantics> entitySemantics,
                          Map<FieldSemantics, SemanticField> semanticFields) {
        this.id = id;
        this.rootElement = rootElement;
        this.entitySemantics = ImmutableSet.copyOf(entitySemantics);
        this.semanticFields = ImmutableMap.copyOf(semanticFields);
    }

    public Localization getLocalization() {
        return localization;
    }

    public void setLocalization(Localization localization) {
        this.localization = localization;
    }

    public long getId() {
        return id;
    }

    public String getRootElement() {
        return rootElement;
    }

    public Set<EntitySemantics> getEntitySemantics() {
        return entitySemantics;
    }

    public Map<FieldSemantics, SemanticField> getSemanticFields() {
        return semanticFields;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SemanticSchema that = (SemanticSchema) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(rootElement, that.rootElement) &&
                Objects.equals(entitySemantics, that.entitySemantics) &&
                Objects.equals(semanticFields, that.semanticFields) &&
                Objects.equals(localization, that.localization);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, rootElement, entitySemantics, semanticFields, localization);
    }

    @Override
    public String toString() {
        return "SemanticSchema{" +
                "id=" + id +
                ", rootElement='" + rootElement + '\'' +
                ", entitySemantics=" + entitySemantics +
                ", semanticFields=" + semanticFields +
                '}';
    }
}
