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
import lombok.extern.slf4j.Slf4j;
import org.joda.time.DateTime;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Base class to generate Syndication Lists.
 */
@Slf4j
public abstract class FeedFormatter extends BaseFormatter {

    FeedFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
    }

    private static boolean isList(SemanticEntity annotation) {
        return (annotation.vocabulary().equals(SemanticVocabulary.SCHEMA_ORG) && annotation.entityName().equals("ItemList"));
    }

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

    @Override
    public Object formatData(Object model) {
        return getData(model);
    }

    /**
     * Gets the feed from the an object.
     */
    protected List<Object> getData(Object model) {
        return model == null ? null : getFeedItemsFromPage((PageModel) model);
    }

    /**
     * Gets the list of syndicated items from a page.
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
     */
    protected List<Object> getFeedItemsFromEntity(EntityModel entity) {
        List<Object> items = new ArrayList<>();
        List<Teaser> entityItems = getEntityItems(entity);
        for (Teaser item : entityItems) {
            try {
                items.add(getSyndicationItemFromTeaser(item));
            } catch (Exception e) {
                log.error("Error getting syndication items from Teaser: {}", e.getMessage());
            }
        }
        return items;
    }

    /**
     * Gets the items from an entity checking its type and depending on it, executes different attempts to produce an Entry.
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
                log.error("Illegal Field Access");
                log.error("Error while getting syndication list: {}", e.getMessage());
            } catch (InvocationTargetException e) {
                log.error("Wrong Invocation of Method");
                log.error("Error while getting syndication list: {}", e.getMessage());
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
                        log.error("Error while instantiating a teaser using reflection for feed: {}", e.getMessage());
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
