package com.sdl.webapp.main.markup;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.model.Entity;

import java.util.*;

public final class Markup {

    private Markup() {
    }

    public static String entity(Entity entity) {
        final StringBuilder sb = new StringBuilder();

        final Map<String, String> prefixes = new HashMap<>();
        final List<String> entityTypes = new ArrayList<>();

        for (SemanticEntity annotation : getSemanticEntityAnnotations(entity.getClass())) {
            if (annotation.public_()) {
                final String prefix = annotation.prefix();
                prefixes.put(prefix, annotation.vocabulary());

                String entityName = annotation.entityName();
                if (Strings.isNullOrEmpty(entityName)) {
                    entityName = annotation.value();
                }

                entityTypes.add(prefix + ":" + entityName);
            }
        }

        if (!prefixes.isEmpty()) {
            final List<String> prefixList = new ArrayList<>();
            for (Map.Entry<String, String> entry : prefixes.entrySet()) {
                prefixList.add(entry.getKey() + ": " + entry.getValue());
            }

            sb.append("prefix=\"").append(Joiner.on(' ').join(prefixList))
                    .append("\" typeof=\"").append(Joiner.on(' ').join(entityTypes)).append("\"");
        }

        // TODO: Some extra stuff when IsPreview is true

        return sb.toString();
    }

    private static List<SemanticEntity> getSemanticEntityAnnotations(Class<? extends Entity> entityClass) {
        SemanticEntities wrapper = entityClass.getAnnotation(SemanticEntities.class);
        if (wrapper != null) {
            return Arrays.asList(wrapper.value());
        } else {
            SemanticEntity annotation = entityClass.getAnnotation(SemanticEntity.class);
            if (annotation != null) {
                return Collections.singletonList(annotation);
            }
        }

        return Collections.emptyList();
    }



    public static String property(Entity entity, String fieldName) {
        return "";
    }

    public static String property(Entity entity, String fieldName, int index) {
        return "";
    }
}
