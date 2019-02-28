package com.sdl.webapp.tridion.linking;

import com.google.common.base.Strings;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.util.TcmUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

@Slf4j
public abstract class AbstractLinkResolver implements LinkResolver {

    @Value("${dxa.web.link-resolver.remove-extension:#{true}}")
    private boolean shouldRemoveExtension;

    @Value("${dxa.web.link-resolver.strip-index-path:#{true}}")
    private boolean shouldStripIndexPath;

    @Override
    @Cacheable(value = "defaultCache", key = "{ #root.methodName,  #url, #localizationId, #resolveToBinary }")
    public String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;

        String resolvedLink = resolveLink(url, publicationId, resolveToBinary);
        String resolvedUrl = shouldStripIndexPath ? PathUtils.stripIndexPath(resolvedLink) : resolvedLink;
        return shouldRemoveExtension ? PathUtils.stripDefaultExtension(resolvedUrl) : resolvedUrl;
    }

    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    private String resolveLink(String uri, int publicationId, boolean isBinary) {
        if (uri == null || !TcmUtils.isTcmUri(uri)) {
            return uri;
        }

        int publicationId1 = publicationId == 0 ? TcmUtils.getPublicationId(uri) : publicationId;
        int itemId = TcmUtils.getItemId(uri);
        ResolvingData resolvingData = new ResolvingData(publicationId1, itemId, uri);

        String result = "";
        switch (TcmUtils.getItemType(uri)) {
            case TcmUtils.COMPONENT_ITEM_TYPE:
                if (isBinary) {
                    result = resolveBinary(resolvingData);
                } else {
                    result = resolveComponent(resolvingData);
                }
                break;
            case TcmUtils.PAGE_ITEM_TYPE:
                result = resolvePage(resolvingData);
                break;
            default:
                log.warn("Could not resolve link: {}", uri);
                return "";
        }

        return result;
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
    }
}
