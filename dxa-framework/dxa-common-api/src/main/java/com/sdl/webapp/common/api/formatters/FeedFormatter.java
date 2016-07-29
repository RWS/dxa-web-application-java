package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.formatters.dto.FeedItem;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.joda.time.DateTime;
import org.springframework.util.Assert;
import org.springframework.util.ReflectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Base class to generate Syndication Lists.
 */
@Slf4j
public abstract class FeedFormatter extends BaseFormatter {

    FeedFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
    }

    /**
     * Gets the items from an entity checking its type and depending on it, executes different attempts to produce an Entry.
     */
    private static void fillWithFeedItemsFromProperties(final EntityModel entity, final LinkedList<FeedItem> list) {

        FeedItem feedItem = new FeedItem();
        list.add(feedItem);

        log.debug("Trying to get feed items from {}", entity);

        ReflectionUtils.doWithMethods(entity.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            @SneakyThrows(InvocationTargetException.class)
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                log.debug("Method {} is a collection, trying to iterate over it and get entities", method);
                for (Object field : ((Collection) method.invoke(entity))) {
                    if (EntityModel.class.isAssignableFrom(field.getClass())) {
                        fillWithFeedItemsFromProperties((EntityModel) field, list);
                    }
                }
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return method.getName().startsWith("get") && Collection.class.isAssignableFrom(method.getReturnType());
            }
        });

        if (!fillFeedItemFromProperties(entity, feedItem)) {
            log.trace("Failed filling FeedItem {}, removing", feedItem);
            list.remove(feedItem);
        }
    }

    private static boolean fillFeedItemFromProperties(final EntityModel entity, final FeedItem feedItem) {
        ReflectionUtils.doWithMethods(entity.getClass(), new ReflectionUtils.MethodCallback() {
            @Override
            @SneakyThrows(InvocationTargetException.class)
            public void doWith(Method method) throws IllegalArgumentException, IllegalAccessException {
                String methodName = method.getName();
                String logMessage = "Method {}#{}() found, but its invocation returned null [default message]";
                switch (methodName.replaceFirst("get(.)", "$1")) {
                    case "Headline":
                    case "Title":
                    case "Name":
                        logMessage = "Found {}#{}(), using it for FeedItem#headline";
                        Object headline = method.invoke(entity);
                        if (headline != null) {
                            feedItem.setHeadline((String) headline);
                        }
                        break;

                    case "Summary":
                    case "Description":
                    case "Snippet":
                    case "Teaser":
                        Object summary = method.invoke(entity);
                        if (summary != null) {
                            logMessage = "Found {}#{}(), using it for FeedItem#summary";
                            feedItem.setSummary(new RichText((String) summary));
                        }
                        break;

                    case "Updated":
                    case "Date":
                        Object date = method.invoke(entity);
                        if (date != null) {
                            logMessage = "Found {}#{}(), using it for FeedItem#date";
                            if (date instanceof DateTime) {
                                feedItem.setDate(((DateTime) date).toDate());
                            } else if (date instanceof Date) {
                                feedItem.setDate((Date) date);
                            } else {
                                log.warn("Class {} is not supported for dates", date.getClass());
                            }
                        }

                        break;

                    case "Url":
                    case "Link":
                        Object methodInvocation = method.invoke(entity);
                        if (methodInvocation != null) {
                            logMessage = "Found {}#{}(), using it for FeedItem#link";
                            Link link;
                            if (Link.class.isAssignableFrom(method.getReturnType())) {
                                link = (Link) methodInvocation;
                            } else {
                                link = new Link();
                                link.setUrl((String) methodInvocation);
                            }
                            feedItem.setLink(link);
                        }
                        break;

                    default:
                        logMessage = "Skipping method {}#{}(), no data for FeedItem here";
                        break;
                }

                log.trace(logMessage, entity.getClass(), methodName);
            }
        }, new ReflectionUtils.MethodFilter() {
            @Override
            public boolean matches(Method method) {
                return method.getName().startsWith("get");
            }
        });

        return feedItem.getHeadline() != null || feedItem.getSummary() != null;
    }

    /**
     * Accepts {@link PageModel} as a model, and processes it to a feed.
     *
     * @param model object, expected to be assignable from {@link PageModel}
     * @return formatted data
     * @throws IllegalArgumentException if the object is not an instance of {@link PageModel}
     */
    @Override
    @Contract("null -> null; !null -> !null")
    public Object formatData(Object model) {
        if (model == null) {
            return null;
        }
        Assert.isInstanceOf(PageModel.class, model, "Model for this formatter expected to be assignable from PageModel");
        return getData(((PageModel) model));
    }

    /**
     * Gets the feed from the a page.
     */
    @Contract("null -> null; !null -> !null")
    protected List<Object> getData(PageModel page) {
        return page == null ? null : getFeedItemsFromPage(page);
    }

    /**
     * Gets the list of syndicated items from a page.
     */
    private List<Object> getFeedItemsFromPage(@NotNull PageModel page) {
        Assert.notNull(page.getRegions());

        List<Object> items = new ArrayList<>();

        for (RegionModel region : page.getRegions()) {
            Assert.notNull(region.getEntities());

            for (EntityModel entity : region.getEntities()) {
                items.addAll(getFeedItemsFromEntity(entity));
            }
        }

        return items;
    }

    /**
     * Gets a list of syndicated items from an Entity.
     */
    private List<Object> getFeedItemsFromEntity(EntityModel entity) {
        LinkedList<FeedItem> entityItems = new LinkedList<>();
        fillWithFeedItemsFromProperties(entity, entityItems);

        List<Object> items = new ArrayList<>(entityItems.size());

        for (FeedItem item : entityItems) {
            try {
                items.add(getSyndicationItem(item));
            } catch (Exception e) {
                log.error("Error getting syndication items from {}", item, e);
            }
        }

        return items;
    }
}
