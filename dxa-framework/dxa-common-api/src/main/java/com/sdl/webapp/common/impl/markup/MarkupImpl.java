package com.sdl.webapp.common.impl.markup;

import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.SemanticMappingRegistry;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntityInfo;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticPropertyInfo;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import com.sdl.webapp.common.controller.exception.NotFoundException;
import com.sdl.webapp.common.markup.Markup;
import com.sdl.webapp.common.markup.html.HtmlAttribute;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.markup.html.builders.SimpleElementBuilder;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.google.common.base.Strings.nullToEmpty;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.a;
import static com.sdl.webapp.common.markup.html.builders.HtmlBuilders.element;

@Component
@Slf4j
public class MarkupImpl implements Markup {

    private static final HtmlAttribute TYPEOF_REGION_ATTR = new HtmlAttribute("typeof", "Region");

    private final SemanticMappingRegistry semanticMappingRegistry;

    private final WebRequestContext webRequestContext;

    /**
     * <p>Constructor for MarkupImpl.</p>
     *
     * @param semanticMappingRegistry a {@link SemanticMappingRegistry} object.
     * @param webRequestContext       a {@link WebRequestContext} object.
     */
    @Autowired
    public MarkupImpl(SemanticMappingRegistry semanticMappingRegistry, WebRequestContext webRequestContext) {
        this.semanticMappingRegistry = semanticMappingRegistry;
        this.webRequestContext = webRequestContext;
    }

    @Nullable
    private static HtmlElement siteMapListHelper(SitemapItem item, SitemapItem root) {

        if (item != null) {
            String url = nullToEmpty(item.getUrl());
            // don't process index paths, we treat them as a duplication of Parent group
            if (PathUtils.isIndexPath(url)) {
                return null;
            }

            final SimpleElementBuilder builder = element("li");

            if (!isNullOrEmpty(url)) {
                builder.withNode(
                        a(url).withTitle(item.getTitle()).withTextualContent(item.getTitle()).build()
                );
            } else {
                builder.withTextualContent(item.getTitle());
            }

            if (item.getItems().isEmpty()) {
                return builder.build();
            }

            SimpleElementBuilder innerUl = element("ul").withClass("list-unstyled");
            for (SitemapItem child : item.getItems()) {
                HtmlElement element = siteMapListHelper(child, root);
                if (element != null) {
                    innerUl.withNode(element);
                }
            }

            builder.withNode(innerUl.build());


            return builder.build();
        }

        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String url(String path) {
        return webRequestContext.getContextPath() + path;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String versionedContent(String path) {
        Localization localization = webRequestContext.getLocalization();
        if (localization == null) {
            throw new NotFoundException("Localization for " + path + " not found.");
        }
        return webRequestContext.getContextPath() + localization.localizePath("/system/" + localization.getVersion() + path);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String region(RegionModel region) {
        return Joiner.on(' ').join(Arrays.asList(
                TYPEOF_REGION_ATTR.toHtml(),
                new HtmlAttribute("resource", region.getName()).toHtml()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String entity(EntityModel entity) {
        final List<String> vocabularies = new ArrayList<>();
        final List<String> entityTypes = new ArrayList<>();

        for (SemanticEntityInfo entityInfo : semanticMappingRegistry.getEntityInfo(entity.getClass())) {
            if (entityInfo.isPublic()) {
                final String prefix = entityInfo.getPrefix();
                if (!Strings.isNullOrEmpty(prefix)) {
                    vocabularies.add(prefix + ": " + entityInfo.getVocabulary());
                    entityTypes.add(prefix + ':' + entityInfo.getEntityName());
                }
            }
        }

        if (!vocabularies.isEmpty()) {
            return new HtmlAttribute("prefix", Joiner.on(' ').join(vocabularies)).toHtml() +
                    ' ' + new HtmlAttribute("typeof", Joiner.on(' ').join(entityTypes)).toHtml();
        }

        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String property(EntityModel entity, String fieldName) {
        return property(entity, fieldName, 0);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String property(EntityModel entity, String fieldName, int index) {

        final Class<? extends EntityModel> entityClass = entity.getClass();

        final Field field = ReflectionUtils.findField(entityClass, fieldName);
        if (field == null) {
            log.warn("Entity of type {} does not contain a field named {}", entityClass.getName(), fieldName);
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
                propertyTypes.add(prefix + ':' + propertyInfo.getPropertyName());
            }
        }

        String markup = "";
        if (!propertyTypes.isEmpty()) {
            markup += new HtmlAttribute("property", Joiner.on(' ').join(propertyTypes)).toHtml();
        }
        if (webRequestContext.isPreview()) {
            final Map<String, String> propertyData = entity.getXpmPropertyMetadata();
            if (propertyData != null) {
                String xpath = propertyData.get(fieldName);
                if (!Strings.isNullOrEmpty(xpath)) {
                    xpath += xpath.endsWith("]") ? "" : ("[" + (index + 1) + ']');
                    markup += ' ' + new HtmlAttribute("data-entity-property-xpath", xpath).toHtml();
                }
            }
        }

        return markup;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String resource(String key) {
        return webRequestContext.getLocalization().getResource(key);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatDate(DateTime dateTime) {
        return DateTimeFormat.fullDate().withLocale(webRequestContext.getLocalization().getLocale()).print(dateTime);
    }

    /**
     * {@inheritDoc}
     */
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String formatMessage(String pattern, Object... args) {
        return MessageFormat.format(pattern, args);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String replaceLineEndsWithHtmlBreaks(String text) {
        return text.replaceAll("\\. ", "<br/>");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String siteMapList(SitemapItem item) {
        //todo dxa2 do this in JSP
        final HtmlElement htmlElement = siteMapListHelper(item, item);
        return htmlElement != null ? htmlElement.toHtml() : "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebRequestContext getWebRequestContext() {
        return webRequestContext;
    }
}
