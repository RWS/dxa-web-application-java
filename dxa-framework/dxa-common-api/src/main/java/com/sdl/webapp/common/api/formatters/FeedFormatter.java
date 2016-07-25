package com.sdl.webapp.common.api.formatters;

import com.sdl.dxa.modules.core.model.entity.ContentList;
import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to generate Syndication Lists
 */
public abstract class FeedFormatter extends BaseFormatter {

    private static final Logger LOG = LoggerFactory.getLogger(FeedFormatter.class);

    /**
     * <p>Constructor for FeedFormatter.</p>
     *
     * @param request a {@link javax.servlet.http.HttpServletRequest} object.
     * @param context a {@link com.sdl.webapp.common.api.WebRequestContext} object.
     */
    public FeedFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
    }

    /**
     * Checks whether a field is a list
     *
     * @param annotation senantic entity annotation
     * @return whether a field is a list
     */
    private static boolean isList(SemanticEntity annotation) {
        return (annotation.vocabulary().equals(SemanticVocabulary.SCHEMA_ORG) && annotation.entityName().equals("ItemList"));

    }

    /**
     * Gets a list of teasers from its semantics
     *
     * @param entity EntityModel
     * @return List<Teaser>
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     */
    private static List<Teaser> getTeaserListFromSemantics(EntityModel entity) throws InvocationTargetException, IllegalAccessException {

        boolean isList = false;
        if (entity.getClass().isAnnotationPresent(SemanticEntity.class)) {
            isList = isList(entity.getClass().getAnnotation(SemanticEntity.class));

        } else if (entity.getClass().isAnnotationPresent(SemanticEntities.class)) {
            if (entity instanceof ContentList && ((ContentList) entity).getLink() != null) {
                //don't treat contentlist if it's on an overview page such as homepage
                isList = false;
            } else {
                SemanticEntities annotations = entity.getClass().getAnnotation(SemanticEntities.class);
                for (SemanticEntity prop : annotations.value()) {
                    isList = isList(prop);
                    if (isList) {
                        break;
                    }
                }
            }
        }

        if (isList) {
            for (Method method : entity.getClass().getDeclaredMethods()) {
                Type genericFieldType = method.getGenericReturnType();
                if (genericFieldType instanceof ParameterizedType) {
                    ParameterizedType pType = (ParameterizedType) genericFieldType;
                    Type[] fieldArgTypes = pType.getActualTypeArguments();
                    for (Type fieldArgType : fieldArgTypes) {
                        Class fieldArgClass = (Class) fieldArgType;
                        if (fieldArgClass.getName().equals(Teaser.class.getName())) {
                            return (List<Teaser>) method.invoke(entity);
                        }

                    }
                }
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     *
     * Returns the formatted data. Additional model processing can be implemented in extending classes
     */
    @Override
    public Object formatData(Object model) {
        return getData(model);
    }

    /**
     * Gets the feed from the an object, checks if it is a @see PageModel
     *
     * @param model a {@link java.lang.Object} object.
     * @return a {@link java.util.List} object.
     */
    protected List<Object> getData(Object model) {
        PageModel page = (PageModel) model;
        if (page != null) {
            return getFeedItemsFromPage(page);
        }
        return null;
    }

    /**
     * Gets the list of syndicated items from a page. @see PageModel
     *
     * @param page page model
     * @return list of feed items
     */
    private List<Object> getFeedItemsFromPage(PageModel page) {
        List<Object> items = new ArrayList<>();
        for (RegionModel region : page.getRegions()) {
            for (EntityModel entity : region.getEntities()) {
                items.addAll(getFeedItemsFromEntity(entity));
            }
        }
        return items;
    }

    /**
     * Gets a list of syndicated items from an Entity.
     *
     * @param entity a {@link com.sdl.webapp.common.api.model.EntityModel} object.
     * @return a {@link java.util.List} object.
     */
    protected List<Object> getFeedItemsFromEntity(EntityModel entity) {
        List<Object> items = new ArrayList<>();
        List<Teaser> entityItems = getEntityItems(entity);
        for (Teaser item : entityItems) {
            try {
                items.add(getSyndicationItemFromTeaser(item));
            } catch (Exception e) {
                LOG.error("Error getting syndication items from Teaser: {}", e.getMessage());
            }
        }
        return items;
    }

    /**
     * Gets the items forn an entity checking its type and depending on it, executes different attempts to produce an Entry
     *
     * @param entity entity model
     * @return list of teasers
     */
    private List<Teaser> getEntityItems(EntityModel entity) {
        List<Teaser> res = new ArrayList<>();
        //1. Check if entity is a teaser, if add it
        if (entity instanceof Teaser) {
            res.add((Teaser) entity);
        } else {
            //2. Second check if entity type is (semantically) a list, and if so, get its list items
            List<Teaser> items = null;
            try {
                items = getTeaserListFromSemantics(entity);

            } catch (IllegalAccessException e) {
                LOG.error("Illegal Field Access");
                LOG.error("Error while getting syndication list: {}", e.getMessage());
            } catch (InvocationTargetException e) {
                LOG.error("Wrong Invocation of Method");
                LOG.error("Error while getting syndication list: {}", e.getMessage());
            }
            if (items != null) {
                res = items;
            } else {

//                3. Last resort, try to find some suitable properties using reflection
                Teaser teaser = new Teaser();

                for (Method m : entity.getClass().getDeclaredMethods()) {
                    if (!m.getName().startsWith("get")) {
                        continue;
                    }
                    try {
                        switch (m.getName().toLowerCase()) {
                            case "getheadline":
                            case "getname":
                                teaser.setHeadline((String) m.invoke(entity));
                                break;
                            case "getdate":
                                DateTime d = (DateTime) m.invoke(entity);
                                teaser.setDate(d);
                                break;
                            case "getdescription":
                                Object desc = m.invoke(entity);
                                if (RichText.class.isInstance(desc)) {
                                    teaser.setText((RichText) desc);
                                } else {
                                    teaser.setText(new RichText(desc.toString()));
                                }
                                break;
                            case "getlink":
                                teaser.setLink((Link) m.invoke(entity));
                                break;
                            case "geturl":
                                String url = (String) m.invoke(entity);
                                if (url != null) {
                                    Link l = new Link();
                                    l.setUrl(url);
                                    teaser.setLink(l);
                                }
                                break;
                            default:
                                break;
                        }
                    } catch (InvocationTargetException | IllegalAccessException e) {
                        LOG.error("Error while instantiating a teaser using reflection for feed: {}", e.getMessage());
                    }
                }

                if (teaser.getHeadline() != null || teaser.getText() != null) {
                    res.add(teaser);
                }
            }
        }
        return res;
    }
}
