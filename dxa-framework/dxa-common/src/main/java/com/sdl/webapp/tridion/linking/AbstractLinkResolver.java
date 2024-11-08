package com.sdl.webapp.tridion.linking;

import com.google.common.base.Strings;
import com.sdl.dxa.caching.NamedCacheProvider;
import com.sdl.dxa.caching.statistics.CacheStatisticsProvider;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.dxa.tridion.annotations.impl.ValueAnnotationLogger;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.util.TcmUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.cache.Cache;
import java.util.UUID;

@Slf4j
@Component
public abstract class AbstractLinkResolver implements LinkResolver, InitializingBean {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLinkResolver.class);

    @Autowired
    private NamedCacheProvider namedCacheProvider;

    @Autowired
    private CacheStatisticsProvider cacheStatisticsProvider;

    @Value("${dxa.web.link-resolver.remove-extension:#{true}}")
    private boolean shouldRemoveExtension;

    @Value("${dxa.web.link-resolver.strip-index-path:#{true}}")
    private boolean shouldStripIndexPath;

    @Value("${dxa.web.link-resolver.keep-trailing-slash:#{false}}")
    private boolean shouldKeepTrailingSlash;

    @Override
    public String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary, @Nullable String contextId) {
        if (!isCacheEnabled()) {
            return processLink(url, localizationId, resolveToBinary, contextId);
        }
        else {
            String cacheKeyInput = String.format("%s%s%b%s", url, localizationId, resolveToBinary, contextId);
            String cacheKey = UUID.nameUUIDFromBytes(cacheKeyInput.getBytes()).toString();
            Cache<Object, Object> resolvedLinksCache = namedCacheProvider.getCache("resolvedLinks");
            String resolvedLink = (String)resolvedLinksCache.get(cacheKey);
            if (resolvedLink == null) {
                resolvedLink = processLink(url, localizationId, resolveToBinary, contextId);
                if (resolvedLink != null) {
                    resolvedLinksCache.put(cacheKey, resolvedLink);
                    if (this.cacheStatisticsProvider != null) {
                        this.cacheStatisticsProvider.storeStatsInfo("resolvedLinks", resolvedLink);
                    }
                }
            }
            return resolvedLink;
        }
    }

    private String processLink(String uri, String localizationId, boolean isBinary, String contextId) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;
        String resolvedLink = _resolveLink(uri, publicationId, isBinary, contextId);
        String resolvedUrl = shouldStripIndexPath ? PathUtils.stripIndexPath(resolvedLink) : resolvedLink;
        if (shouldKeepTrailingSlash && (! "/".equals(resolvedUrl)) && PathUtils.isIndexPath(resolvedLink)) {
            resolvedUrl = resolvedUrl + "/";
        }
        return shouldRemoveExtension ? PathUtils.stripDefaultExtension(resolvedUrl) : resolvedUrl;
    }

    @Contract("null, _, _ , null-> null; !null, _, _, !null -> !null")
    private String _resolveLink(String uri, int publicationId, boolean isBinary, String contextId) {
        if (!TcmUtils.isTcmUri(uri)) {
            return uri;
        }

        //Page ID is either tcm uri or int (in string form) -1 means no page context
        int pageId = getPageId(contextId);

        int itemId = TcmUtils.getItemId(uri);

        ResolvingData resolvingData;
        if (publicationId <= 0) {
            publicationId = TcmUtils.getPublicationId(uri);
        }
        resolvingData = new ResolvingData(publicationId, itemId, uri, pageId);

        switch (TcmUtils.getItemType(uri)) {
            case TcmUtils.COMPONENT_ITEM_TYPE:
                if (isBinary) {
                    return resolveBinary(resolvingData);
                }
                return resolveComponent(resolvingData);
            case TcmUtils.PAGE_ITEM_TYPE:
                return resolvePage(resolvingData);
            default:
                log.warn("Could not resolve {}link: {} in pub: {}", isBinary?"binary ":"", uri, publicationId);
                return "";
        }
    }

    private boolean isCacheEnabled() {
        return namedCacheProvider != null && namedCacheProvider.isCacheEnabled("resolvedLinks");
    }

    private int getPageId(String contextId) {
        int pageId;
        if (TcmUtils.isTcmUri(contextId)) {
            pageId = TcmUtils.getItemId(contextId);
        } else {
            pageId = NumberUtils.toInt(contextId, -1);
        }
        return pageId;
    }

    protected abstract String resolveComponent(ResolvingData resolvingData);

    protected abstract String resolvePage(ResolvingData resolvingData);

    protected abstract String resolveBinary(ResolvingData resolvingData);

    @AllArgsConstructor
    @Getter
    protected static class ResolvingData {

        private int publicationId;

        private int itemId;

        private String uri;

        private int pageId;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOG.info(new ValueAnnotationLogger().fetchAllValues(this));
    }
}
