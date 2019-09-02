package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.exceptions.DxaException;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.ambientdata.web.WebContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
     * Actually getting the page model from the backend is done in loadPage.
     *
     * @param path path
     * @param localization Localization object
     * @return page model
     * @throws ContentProviderException in case of bad request
     */
    public PageModel getPageModel(String path, Localization localization) throws ContentProviderException {
        Assert.notNull(localization);

        String key = createKeyForCacheByPath(path, localization, "pagemodel");
        long time = System.currentTimeMillis();

        SimpleValueWrapper simpleValueWrapper = null;
        if (!webRequestContext.isSessionPreview()) {
            simpleValueWrapper = (SimpleValueWrapper) pagemodelCache.get(key);
        }

        PageModel pageModel;
        if (simpleValueWrapper != null) {
            //Pagemodel is in cache
            pageModel = (PageModel) simpleValueWrapper.get();
        } else {
            //Not in cache, load from backend.
            pageModel = loadPage(path, localization);
            if (pageModel.canBeCached() && !webRequestContext.isSessionPreview()) {
                pagemodelCache.put(key, pageModel);
                pagemodelCache.put(createKeyForCacheByPath(pageModel.getId(), localization, "pagemodel"), pageModel);
            }
        }
        try {
            // Make a deep copy
            pageModel = pageModel.deepCopy();
        } catch (DxaException e) {
            throw new ContentProviderException("PageModel for " + key + " cannot be copied", e);
        }

        //filterConditionalEntities modifies the pagemodel, that is why the deep copy is done.
        pageModel.filterConditionalEntities(entityEvaluators);

        webRequestContext.setPage(pageModel);

        if (log.isDebugEnabled()) {
            log.debug("Page model {}{} [{}] loaded. (Cacheable: {}), loading took {} ms. ",
                    pageModel.getUrl(),
                    pageModel.getId(),
                    pageModel.getName(),
                    pageModel.canBeCached(),
                    (System.currentTimeMillis() - time));
        }

        return pageModel;
    }

    @NotNull
    private String createKeyForCacheByPath(String path, Localization localization, String type) {
        return type + " [" + path + "] " + localization.getId();
    }

    @NotNull
    private String createKeyForCacheById(String id, Localization localization, String type) {
        return createKeyForCacheByPath("[" + id + "]", localization, type);
    }

    /**
     * This default implementation handles caching and cloning the pagemodel.
     * Actually getting the pagemodel from the backend is done in loadPage.
     *
     * @param pageId page ID
     * @param localization Localization object
     * @return page model
     * @throws ContentProviderException in case of bad request
     */
    public PageModel getPageModel(int pageId, Localization localization) throws ContentProviderException {
        Assert.notNull(localization);

        String key = createKeyForCacheById("" + pageId, localization, "pagemodel");;
        long time = System.currentTimeMillis();

        SimpleValueWrapper simpleValueWrapper = null;
        if (!webRequestContext.isSessionPreview()) {
            simpleValueWrapper = (SimpleValueWrapper) pagemodelCache.get(key);
        }

        PageModel pageModel;
        if (simpleValueWrapper != null) {
            //Pagemodel is in cache
            pageModel = (PageModel) simpleValueWrapper.get();
        } else {
            //Not in cache, load from backend.
            pageModel = loadPage(pageId, localization);
            if (pageModel.canBeCached() && !webRequestContext.isSessionPreview()) {
                pagemodelCache.put(key, pageModel);
                pagemodelCache.put(createKeyForCacheByPath(pageModel.getUrl(), localization, "pagemodel"), pageModel);
            }
        }
        try {
            // Make a deep copy
            pageModel = pageModel.deepCopy();
        } catch (DxaException e) {
            throw new ContentProviderException("PageModel for " + key + " cannot be copied", e);
        }

        //filterConditionalEntities modifies the pagemodel, that is why the deep copy is done.
        pageModel.filterConditionalEntities(entityEvaluators);

        webRequestContext.setPage(pageModel);

        if (log.isDebugEnabled()) {
            log.debug("Page model {}{} [{}] loaded. (Cacheable: {}), loading took {} ms. ",
                    pageModel.getUrl(),
                    pageModel.getId(),
                    pageModel.getName(),
                    pageModel.canBeCached(),
                    (System.currentTimeMillis() - time));
        }

        return pageModel;
    }

    /**
     * Create a cache key for the current claims.
     * @return cache key
     */
    private String getClaimCacheKey() {
        ClaimStore currentClaimStore = WebContext.getCurrentClaimStore();
        if (currentClaimStore == null || currentClaimStore.getClaimValues() == null) {
            return "noclaims";
        }
        return " claims:" + currentClaimStore.getClaimValues().entrySet().stream().map(Object::toString).collect(Collectors.joining(","));
    }

    abstract PageModel loadPage(String path, Localization localization) throws ContentProviderException;
    abstract PageModel loadPage(int pageId, Localization localization) throws ContentProviderException;

    /**
     * {@inheritDoc}
     *
     * @dxa.publicApi
     */
    public EntityModel getEntityModel(@NotNull String id, Localization localization) throws ContentProviderException {
        Assert.notNull(id);
        long time = System.currentTimeMillis();
        String key = createKeyForCacheById(id, localization, "entitymodel");
        SimpleValueWrapper simpleValueWrapper = null;
        if (!webRequestContext.isSessionPreview()) {
            simpleValueWrapper = (SimpleValueWrapper) entitymodelCache.get(key);
        }
        EntityModel entityModel = null;
        if (simpleValueWrapper != null) {
            //EntityModel is in cache
            entityModel = (EntityModel) simpleValueWrapper.get();
        } else {
            //Not in cache, load from backend.
            entityModel = getEntityModel(id);
            if (entityModel.getXpmMetadata() != null) {
                entityModel.getXpmMetadata().put("IsQueryBased", true);
            }
            if (entityModel.canBeCached() && !webRequestContext.isSessionPreview()) {
                entitymodelCache.put(key, entityModel);
            }
        }

        try {
            //Return a deepcopy so controllers can dynamicly change the content without causing problems.
            entityModel = entityModel.deepCopy();
        } catch (DxaException e) {
            throw new ContentProviderException("EntityModel for " + key + " cannot be copied", e);
        }
        if (log.isDebugEnabled()) {
            log.debug("Entity model {} loaded. (Cacheable: {}), loading took {} ms. ",
                    entityModel.getId(),
                    entityModel.canBeCached(),
                    (System.currentTimeMillis() - time));
        }
        return entityModel;
    }

    protected abstract EntityModel getEntityModel(String componentId) throws ContentProviderException;
}
