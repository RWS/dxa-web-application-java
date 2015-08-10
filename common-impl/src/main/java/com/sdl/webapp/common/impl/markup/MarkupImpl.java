package com.sdl.webapp.common.impl.markup;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.Region;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.markup.Markup;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.HtmlBuilders;
import com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder;
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
public class MarkupImpl implements Markup {
    private static final Logger LOG = LoggerFactory.getLogger(MarkupImpl.class);

    private static final HtmlAttribute TYPEOF_REGION_ATTR = new HtmlAttribute("typeof", "Region");

    private final SemanticMappingRegistry semanticMappingRegistry;

    private final WebRequestContext webRequestContext;

    @Autowired
    public MarkupImpl(SemanticMappingRegistry semanticMappingRegistry, WebRequestContext webRequestContext) {
        this.semanticMappingRegistry = semanticMappingRegistry;
        this.webRequestContext = webRequestContext;
    }

    @Override
    public String url(String path) {
        return webRequestContext.getContextPath() + path;
    }

    @Override
    public String versionedContent(String path) {
        return webRequestContext.getContextPath() + webRequestContext.getLocalization().localizePath(
                "/system/" + webRequestContext.getLocalization().getVersion() + path);
    }

    @Override
    public String region(Region region) {
        return Joiner.on(' ').join(Arrays.asList(
                TYPEOF_REGION_ATTR.toHtml(),
                new HtmlAttribute("resource", region.getName()).toHtml()));
    }

    @Override
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

    @Override
    public String property(Entity entity, String fieldName) {
        return property(entity, fieldName, 0);
    }

    @Override
    public String property(Entity entity, String fieldName, int index) {

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

        String markup = "";
        if (!propertyTypes.isEmpty()) {
            markup += new HtmlAttribute("property", Joiner.on(' ').join(propertyTypes)).toHtml();
        }
        if ( webRequestContext.isPreview() ) {
            final Map<String, String> propertyData = entity.getPropertyData();
            String xpath = propertyData.get(fieldName);
            if (!Strings.isNullOrEmpty(xpath)) {
                xpath += xpath.endsWith("]") ? "" : ("[" + (index + 1) + "]");
                markup += " " + new HtmlAttribute("data-entity-property-xpath", xpath).toHtml();
            }
        }

        return markup;
    }

    @Override
    public String resource(String key) {
        return webRequestContext.getLocalization().getResource(key);
    }

    @Override
    public String formatDate(DateTime dateTime) {
        return DateTimeFormat.fullDate().withLocale(webRequestContext.getLocalization().getLocale()).print(dateTime);
    }

    @Override
    public String formatDateDiff(DateTime dateTime) {
        final int dayDiff = Days.daysBetween(dateTime.toLocalDate(), LocalDate.now()).getDays();
        if (dayDiff <= 0) {
            return webRequestContext.getLocalization().getResource("core.todayText");
        } else if (dayDiff == 1) {
            return webRequestContext.getLocalization().getResource("core.yesterdayText");
        } else if (dayDiff <= 7) {
            return MessageFormat.format(webRequestContext.getLocalization().getResource("core.xDaysAgoText"), dayDiff);
        }

        return DateTimeFormat.forPattern("d MMM yyyy").withLocale(webRequestContext.getLocalization().getLocale())
                .print(dateTime);
    }

    @Override
    public String formatMessage(String pattern, Object... args) {
        return MessageFormat.format(pattern, args);
    }

    @Override
    public String replaceLineEndsWithHtmlBreaks(String text) {
        return text.replaceAll("\\. ", "<br/>");
    }

    @Override
    public String siteMapList(SitemapItem item) {
        final HtmlElement htmlElement = siteMapListHelper(item);
        return htmlElement != null ? htmlElement.toHtml() : "";
    }

    private HtmlElement siteMapListHelper(SitemapItem item) {
        if (!item.getUrl().endsWith("/index")) {
            final SimpleElementBuilder itemElementBuilder = HtmlBuilders.element("li")
                    .withContent(HtmlBuilders.a(item.getUrl()).withTitle(item.getTitle()).withContent(item.getTitle())
                            .build());

            if (!item.getItems().isEmpty()) {
                final SimpleElementBuilder childListBuilder = HtmlBuilders.element("ul").withClass("list-unstyled");
                for (SitemapItem child : item.getItems()) {
                    final HtmlElement childElement = siteMapListHelper(child);
                    if (childElement != null) {
                        childListBuilder.withContent(childElement);
                    }
                }
                itemElementBuilder.withContent(childListBuilder.build());
            }

            return itemElementBuilder.build();
        }

        return null;
    }
}
