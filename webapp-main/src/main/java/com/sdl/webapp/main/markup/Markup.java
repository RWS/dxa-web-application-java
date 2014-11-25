package com.sdl.webapp.main.markup;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.main.markup.html.HtmlAttribute;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.*;

@Component
public class Markup {
    private static final Logger LOG = LoggerFactory.getLogger(Markup.class);

    private static final HtmlAttribute TYPEOF_REGION_ATTR = new HtmlAttribute("typeof", "Region");

    private final SemanticMappingRegistry semanticMappingRegistry;

    private final WebRequestContext webRequestContext;

    @Autowired
    public Markup(SemanticMappingRegistry semanticMappingRegistry, WebRequestContext webRequestContext) {
        this.semanticMappingRegistry = semanticMappingRegistry;
        this.webRequestContext = webRequestContext;
    }

    public String url(String path) {
        return webRequestContext.getContextPath() + path;
    }

    public String versionedContent(String path) {
        return webRequestContext.getContextPath() + "/system/" + webRequestContext.getLocalization().getVersion() + path;
    }

    public String region(Region region) {
        return Joiner.on(' ').join(Arrays.asList(
                TYPEOF_REGION_ATTR.toHtml(),
                new HtmlAttribute("resource", region.getName()).toHtml()));
    }

    public String entity(Entity entity) {
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
            return new HtmlAttribute("prefix", Joiner.on(' ').join(vocabularies)).toHtml() +
                    ' ' + new HtmlAttribute("typeof", Joiner.on(' ').join(entityTypes)).toHtml();
        }

        return "";
    }

    public String property(Entity entity, String fieldName) {
        final Class<? extends Entity> entityClass = entity.getClass();

        final Field field = ReflectionUtils.findField(entityClass, fieldName);
        if (field == null) {
            LOG.warn("Entity of type {} does not contain a field named {}", entityClass.getName(), fieldName);
            return "";
        }

        final Set<String> publicPrefixes = new HashSet<>();
        for (SemanticEntityInfo entityInfo : semanticMappingRegistry.getEntityInfo(entityClass)) {
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
            return new HtmlAttribute("property", Joiner.on(' ').join(propertyTypes)).toHtml();
        }

        return "";
    }

    public String resource(String key) {
        return webRequestContext.getLocalization().getResource(key);
    }

    public String formatDateTime(DateTime dateTime, String pattern) {
        return DateTimeFormat.forPattern(pattern).print(dateTime);
    }

    public String formatDateTimeDiff(DateTime dateTime, String pattern) {
        final int dayDiff = Days.daysBetween(dateTime.toLocalDate(), LocalDate.now()).getDays();
        if (dayDiff <= 0) {
            return webRequestContext.getLocalization().getResource("core.todayText");
        } else if (dayDiff == 1) {
            return webRequestContext.getLocalization().getResource("core.yesterdayText");
        } else if (dayDiff <= 7) {
            return MessageFormat.format(webRequestContext.getLocalization().getResource("core.xDaysAgoText"), dayDiff);
        }

        return DateTimeFormat.forPattern(pattern).print(dateTime);
    }

    public String formatMessage(String pattern, String arg) {
        return MessageFormat.format(pattern, arg);
    }
}
