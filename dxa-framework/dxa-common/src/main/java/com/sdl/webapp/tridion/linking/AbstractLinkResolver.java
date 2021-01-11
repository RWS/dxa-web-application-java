package com.sdl.webapp.tridion.linking;

import com.google.common.base.Strings;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public abstract class AbstractLinkResolver implements LinkResolver, InitializingBean {
    private static final Logger LOG = LoggerFactory.getLogger(AbstractLinkResolver.class);

    @Value("${dxa.web.link-resolver.remove-extension:#{true}}")
    private boolean shouldRemoveExtension;

    @Value("${dxa.web.link-resolver.strip-index-path:#{true}}")
    private boolean shouldStripIndexPath;

    @Override
    public String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary, @Nullable String contextId) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;

        String resolvedLink = _resolveLink(url, publicationId, resolveToBinary, contextId);
        String resolvedUrl = shouldStripIndexPath ? PathUtils.stripIndexPath(resolvedLink) : resolvedLink;
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
