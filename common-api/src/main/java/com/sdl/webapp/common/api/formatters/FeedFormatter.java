package com.sdl.webapp.common.api.formatters;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntities;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.config.SemanticVocabulary;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.RegionModel;
import com.sdl.webapp.common.api.model.entity.Teaser;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 9/29/2015.
 */
public abstract class FeedFormatter extends BaseFormatter {


    public FeedFormatter(HttpServletRequest request, WebRequestContext context) {
        super(request, context);
    }

    @Override
    public Object formatData(Object model) {
        return getData(model);
    }

    protected List<Object> getData(Object model)
    {
        PageModel page = (PageModel)model;
        if (page!=null)
        {
            return getFeedItemsFromPage(page);
        }
        return null;
    }

    private List<Object> getFeedItemsFromPage(PageModel page)
    {
        List<Object> items = new ArrayList<Object>();
        for (RegionModel region : page.getRegions())
        {
            for (EntityModel entity : region.getEntities())
            {
                items.addAll(getFeedItemsFromEntity(entity));
            }
        }
        return items;
    }

    protected List<Object> getFeedItemsFromEntity(EntityModel entity)
    {
        List<Object> items = new ArrayList<Object>();
        List<Teaser> entityItems = getEntityItems(entity);
        for(Teaser item : entityItems)
        {
            try {
                items.add(getSyndicationItemFromTeaser(item));
            } catch (URISyntaxException e) {
                //TODO: log error
            }
        }
        return items;
    }

    private List<Teaser> getEntityItems(EntityModel entity)
    {
        List<Teaser> res = new ArrayList<Teaser>();
        //1. Check if entity is a teaser, if add it
        if (entity instanceof Teaser)
        {
            res.add((Teaser) entity);
        }
        else
        {
            //2. Second check if entity type is (semantically) a list, and if so, get its list items
            List<Teaser> items = null;
            try {
                items = getTeaserListFromSemantics(entity);
            } catch (IllegalAccessException e) {
                //TODO: log it
            } catch (InvocationTargetException e) {
                //TODO: log it
            }
            if (items != null)
            {
                res = items;
            }
            else
            {
                //TODO: use reflection
                //3. Last resort, try to find some suitable properties using reflection
//                Teaser teaser = new Teaser();
//                foreach (PropertyInfo pi in entity.GetType().GetProperties())
//                {
//                    switch (pi.Name)
//                    {
//                        case "Headline":
//                        case "Name":
//                            teaser.Headline = pi.GetValue(entity) as String;
//                            break;
//                        case "Date":
//                            DateTime? date = pi.GetValue(entity) as DateTime?;
//                            if (date != null)
//                                teaser.Date = date;
//                            break;
//                        case "Description":
//                            teaser.Text = pi.GetValue(entity) as String;
//                            break;
//                        case "Link":
//                            teaser.Link = pi.GetValue(entity) as Link;
//                            break;
//                        case "Url":
//                            string url = pi.GetValue(entity) as String;
//                            if (url != null)
//                                teaser.Link = new Link { Url = url };
//                        break;
//                    }
//                }
//                if (teaser.Headline != null || teaser.Text != null || teaser.Link != null)
//                {
//                    res.Add(teaser);
//                }
            }
        }
        return res;
    }
    private boolean isList(SemanticEntity annotation){
        return (annotation.vocabulary().equals(SemanticVocabulary.SCHEMA_ORG) && annotation.entityName().equals("ItemList"));

    }

    private List<Teaser> getTeaserListFromSemantics(EntityModel entity) throws InvocationTargetException, IllegalAccessException {

        boolean isList = false;
        if (entity.getClass().isAnnotationPresent(SemanticEntity.class))
        {
            isList = isList(entity.getClass().getAnnotation(SemanticEntity.class));

        }else if(entity.getClass().isAnnotationPresent(SemanticEntities.class)){
            SemanticEntities annotations = entity.getClass().getAnnotation(SemanticEntities.class);
            for(SemanticEntity prop : annotations.value()){
                isList = isList(prop);
                if(isList){
                    break;
                }
            }
        }

        if (isList)
        {
            for(Method method :  entity.getClass().getDeclaredMethods()){
                Type genericFieldType = method.getGenericReturnType();
                if(genericFieldType instanceof ParameterizedType){
                    ParameterizedType pType = (ParameterizedType)genericFieldType;
                    Type[] fieldArgTypes = pType.getActualTypeArguments();
                    for(Type fieldArgType : fieldArgTypes){
                        Class fieldArgClass = (Class) fieldArgType;
                        if(fieldArgClass.getName().equals(Teaser.class.getName())){
                            return (List<Teaser>)method.invoke(entity);
                        }

                    }
                }
            }
        }
        return null;
    }


}
