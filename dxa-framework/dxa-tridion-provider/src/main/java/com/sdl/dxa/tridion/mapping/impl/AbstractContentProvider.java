package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;

@Slf4j
public abstract class AbstractContentProvider {

    private List<ConditionalEntityEvaluator> entityEvaluators = Collections.emptyList();
    protected WebRequestContext webRequestContext;
    private final Cache pagemodelCache;
    private final Cache entitymodelCache;

    protected AbstractContentProvider(WebRequestContext webRequestContext, CacheManager cacheManager) {
        this.webRequestContext = webRequestContext;
        this.pagemodelCache = cacheManager.getCache("pageModels");
        this.entitymodelCache = cacheManager.getCache("entityModels");
    }

    @Autowired(required = false)
    public void setEntityEvaluators(List<ConditionalEntityEvaluator> entityEvaluators) {
        this.entityEvaluators = entityEvaluators;
    }


    /**
     * This default implementation handles caching and cloning the pagemodel.
     * Actually getting the pagemodel from the backend is done in loadPage.
     *
     * @param path
     * @param localization
     * @return
     * @throws ContentProviderException
     */
    public PageModel getPageModel(String path, Localization localization) throws ContentProviderException {
        Assert.notNull(localization);

        String key = "pagemodel" + path + " " + localization.getId();
        long time = System.currentTimeMillis();

        SimpleValueWrapper simpleValueWrapper = (SimpleValueWrapper) pagemodelCache.get(key);
        PageModel pageModel;
        if (simpleValueWrapper != null) {
            //Pagemodel is in cache
            pageModel = (PageModel) simpleValueWrapper.get();
        } else {
            //Not in cache, load from backend.
            pageModel = loadPage(path, localization);
            if (pageModel.canBeCached()) {
                pagemodelCache.put(key, pageModel);
            }
        }
        try {
            // Make a deep copy
            pageModel = pageModel.deepCopy();
        } catch (DxaException e) {
            throw new ContentProviderException(e);
        }

        //filterConditionalEntities modifies the pagemodel, that is why the deep copy is done.
        pageModel.filterConditionalEntities(entityEvaluators);

        webRequestContext.setPage(pageModel);

        if (log.isDebugEnabled()) {
            log.debug("Page model {}{} [{}] loaded. (Cachable: {}), loading took {} ms. ",
                    pageModel.getUrl(),
                    pageModel.getId(),
                    pageModel.getName(),
                    pageModel.canBeCached(),
                    (System.currentTimeMillis() - time));
        }

        return pageModel;
    }

    abstract PageModel loadPage(String path, Localization localization) throws ContentProviderException;

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    public EntityModel getEntityModel(@NotNull String id, Localization localization) throws ContentProviderException {
        Assert.notNull(id);

        String key = id;
        SimpleValueWrapper simpleValueWrapper = (SimpleValueWrapper) entitymodelCache.get(key);
        EntityModel entityModel;
        if (simpleValueWrapper != null) {
            //EntityModel is in cache
            entityModel = (EntityModel) simpleValueWrapper.get();
        } else {
            //Not in cache, load from backend.
            entityModel = getEntityModel(id);
            if (entityModel.getXpmMetadata() != null) {
                entityModel.getXpmMetadata().put("IsQueryBased", true);
            }
            if (entityModel.canBeCached()) {
                entitymodelCache.put(key, entityModel);
            }
        }

        try {
            //Return a deepcopy so controllers can dynamicly change the content without causing problems.
            return entityModel.deepCopy();
        } catch (DxaException e) {
            throw new ContentProviderException(e);
        }
    }

    protected abstract EntityModel getEntityModel(String componentId) throws ContentProviderException;
}
