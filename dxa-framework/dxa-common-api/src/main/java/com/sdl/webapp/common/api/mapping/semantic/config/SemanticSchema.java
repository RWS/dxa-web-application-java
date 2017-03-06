package com.sdl.webapp.common.api.mapping.semantic.config;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.sdl.webapp.common.api.localization.Localization;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
public final class SemanticSchema {

    @Getter
    private final long id;

    @Getter
    private final String rootElement;

    @Getter
    private final Set<EntitySemantics> entitySemantics;

    @Getter
    private final Map<FieldSemantics, SemanticField> semanticFields;

    @Getter
    @Setter
    private Localization localization;

    public SemanticSchema(long id, String rootElement, Set<EntitySemantics> entitySemantics,
                          Map<FieldSemantics, SemanticField> semanticFields) {
        this.id = id;
        this.rootElement = rootElement;
        this.entitySemantics = ImmutableSet.copyOf(entitySemantics);
        this.semanticFields = ImmutableMap.copyOf(semanticFields);
    }

    public Set<String> getFullyQualifiedNames() {
        final HashSet<String> result = new HashSet<>();
        for (EntitySemantics semantics : this.entitySemantics) {
            result.add(String.format("%s:%s", semantics.getVocabulary().getId(), semantics.getEntityName()));
        }
        return result;
    }

    /**
     * Checks whether the current semantic schema has a given entity semantics.
     *
     * @param semantics entity semantics to test against
     * @return whether the current schema has given semantics
     */
    public boolean hasSemantics(EntitySemantics semantics) {
        return entitySemantics.contains(semantics);
    }
}
