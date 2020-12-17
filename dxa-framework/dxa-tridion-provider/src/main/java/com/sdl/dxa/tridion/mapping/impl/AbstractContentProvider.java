package com.sdl.dxa.tridion.mapping.impl;

import com.sdl.dxa.common.ClaimValues;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.exceptions.DxaRuntimeException;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.ambientdata.web.WebContext;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
        PageModel pageModel = null;
        long time = System.currentTimeMillis();
        try {
            Assert.notNull(localization);
            String key = createKeyForCacheByPath(path, localization, "pagemodel");
            SimpleValueWrapper simpleValueWrapper = null;
            if (!webRequestContext.isSessionPreview()) {
                simpleValueWrapper = (SimpleValueWrapper) pagemodelCache.get(key);
            }
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
            } catch (DxaRuntimeException e) {
                throw new ContentProviderException("PageModel for " + key + " cannot be copied", e);
            }
            //filterConditionalEntities modifies the pagemodel, that is why the deep copy is done.
            pageModel.filterConditionalEntities(entityEvaluators);

            webRequestContext.setPage(pageModel);
            return pageModel;
        } finally {
            if (pageModel != null && log.isDebugEnabled()) {
                log.debug("Page model {}{} [{}] loaded. (Cacheable: {}), loading took {} ms. ",
                        pageModel.getUrl(),
                        pageModel.getId(),
                        pageModel.getName(),
                        pageModel.canBeCached(),
                        (System.currentTimeMillis() - time));
            }
        }
    }

    @NotNull
    private String createKeyForCacheByPath(String path, Localization localization, String type) {
        return type + " [" + path + "] " + localization.getId() + getClaimCacheKey() ;
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
        PageModel pageModel = null;
        long time = System.currentTimeMillis();
        try {
            Assert.notNull(localization);
            String key = createKeyForCacheById("" + pageId, localization, "pagemodel");

            SimpleValueWrapper simpleValueWrapper = null;
            if (!webRequestContext.isSessionPreview()) {
                simpleValueWrapper = (SimpleValueWrapper) pagemodelCache.get(key);
            }
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
            } catch (DxaRuntimeException e) {
                throw new ContentProviderException("PageModel for " + key + " cannot be copied", e);
            }
            //filterConditionalEntities modifies the pagemodel, that is why the deep copy is done.
            pageModel.filterConditionalEntities(entityEvaluators);

            webRequestContext.setPage(pageModel);
            return pageModel;
        } finally {
            if (pageModel != null && log.isDebugEnabled()) {
                log.debug("Page model {} [{}] loaded. (Cacheable: {}), loading took {} ms. ",
                        pageModel.getUrl(),
                        pageModel.getName(),
                        pageModel.canBeCached(),
                        (System.currentTimeMillis() - time));
            }
        }
    }

    /**
     * Create a cache key for the current claims.
     * @return cache key
     */
    private String getClaimCacheKey() {
        ClaimStore currentClaimStore = WebContext.getCurrentClaimStore();
        if (currentClaimStore == null) {
            return " noclaims";
        }
        Map<URI, Object> claimValues = currentClaimStore.getClaimValues();
        if (claimValues == null || claimValues.isEmpty()) {
            return " noclaims";
        }
        String conditions = claimValues
                .entrySet()
                .stream()
                .map(Object::toString)
                .filter(obj -> obj.startsWith(ClaimValues.ISH_CONDITIONS))
                .collect(java.util.stream.Collectors.joining(","));
        //remove all <prop>=null properties
        conditions = conditions
                .replaceAll("([ ,{])([_0-9-]|\\pL)++=null", "$1<-n->")
                .replaceAll("(,\\s<-n->)++", ",<-n->")
                .replaceAll(",<-n->,","\n\n")
                .replaceAll("\\{<-n->\\n", "{");
        return com.google.common.base.Strings.isNullOrEmpty(conditions) ? " noclaims" : " claims:" + conditions;
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
            //Return a deep copy so controllers can dynamically change the content without causing problems.
            entityModel = entityModel.deepCopy();
        } catch (DxaRuntimeException e) {
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
