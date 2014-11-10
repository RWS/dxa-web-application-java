package com.sdl.webapp.main.markup;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.main.markup.html.HtmlAttribute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class Markup {
    private static final Logger LOG = LoggerFactory.getLogger(Markup.class);

    private final SemanticMappingRegistry semanticMappingRegistry;

    @Autowired
    public Markup(SemanticMappingRegistry semanticMappingRegistry) {
        this.semanticMappingRegistry = semanticMappingRegistry;
    }

    public String entity(Entity entity) {
        final StringBuilder sb = new StringBuilder();

        final List<String> vocabularies = new ArrayList<>();
        final List<String> entityTypes = new ArrayList<>();

        for (SemanticEntityInfo entityInfo : semanticMappingRegistry.getEntityInfo(entity.getClass())) {
            if (entityInfo.isPublic()) {
                final String prefix = entityInfo.getPrefix();
                if (!Strings.isNullOrEmpty(prefix)) {
                    vocabularies.add(prefix + ": " + entityInfo.getVocabulary());
                    entityTypes.add(prefix + ":" + entityInfo.getEntityName());
                }
            }
        }

        if (!vocabularies.isEmpty()) {
            sb.append(new HtmlAttribute("prefix", Joiner.on(' ').join(vocabularies)).toHtml()).append(' ')
                    .append(new HtmlAttribute("typeof", Joiner.on(' ').join(entityTypes)).toHtml());
        }

        return sb.toString();
    }

    public String property(Entity entity, String fieldName) {
        final Field field = ReflectionUtils.findField(entity.getClass(), fieldName);
        if (field == null) {
            LOG.warn("Entity of type {} does not contain a field named {}", entity.getClass().getName(), fieldName);
            return "";
        }

        final StringBuilder sb = new StringBuilder();

        final Set<String> publicPrefixes = new HashSet<>();
        for (SemanticEntityInfo entityInfo : semanticMappingRegistry.getEntityInfo(entity.getClass())) {
            if (entityInfo.isPublic()) {
                final String prefix = entityInfo.getPrefix();
                if (!Strings.isNullOrEmpty(prefix)) {
                    publicPrefixes.add(prefix);
                }
            }
        }

        final List<String> propertyTypes = new ArrayList<>();
        for (SemanticPropertyInfo propertyInfo : semanticMappingRegistry.getPropertyInfo(field)) {
            final String prefix = propertyInfo.getPrefix();
            if (publicPrefixes.contains(prefix)) {
                propertyTypes.add(prefix + ":" + propertyInfo.getPropertyName());
            }
        }

        if (!propertyTypes.isEmpty()) {
            sb.append(new HtmlAttribute("property", Joiner.on(' ').join(propertyTypes)).toHtml());
        }

        return sb.toString();
    }
}
