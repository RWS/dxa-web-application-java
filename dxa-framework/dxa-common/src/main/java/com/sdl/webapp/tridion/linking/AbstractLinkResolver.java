package com.sdl.webapp.tridion.linking;

import com.google.common.base.Strings;
import com.sdl.dxa.common.util.PathUtils;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.util.TcmUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.math.NumberUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;

import java.util.Optional;
import java.util.function.Function;

@Slf4j
public abstract class AbstractLinkResolver implements LinkResolver {

    @Value("${dxa.web.link-resolver.remove-extension:#{true}}")
    private boolean shouldRemoveExtension;

    @Value("${dxa.web.link-resolver.strip-index-path:#{true}}")
    private boolean shouldStripIndexPath;

    @Override
    @Cacheable(value = "defaultCache", key = "{ #root.methodName,  #url, #localizationId, #resolveToBinary, #contextId }")
    public String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary, @Nullable String contextId) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;

        String resolvedLink = _resolveLink(url, publicationId, resolveToBinary, contextId);
        String resolvedUrl = shouldStripIndexPath ? PathUtils.stripIndexPath(resolvedLink) : resolvedLink;
        return shouldRemoveExtension ? PathUtils.stripDefaultExtension(resolvedUrl) : resolvedUrl;
    }

    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    private String _resolveLink(String uri, int publicationId, boolean isBinary, String contextId) {
        if (uri == null || !TcmUtils.isTcmUri(uri)) {
            return uri;
        }
        //Page ID is either tcm uri or int (in string form) -1 means no page context
        int pageId = -1;
        if (contextId != null && TcmUtils.isTcmUri(contextId)) {
            pageId = TcmUtils.getItemId(contextId);
        }
        else{
            pageId = NumberUtils.toInt(contextId,-1);
        }
        Function<ResolvingData, Optional<String>> resolver;
        switch (TcmUtils.getItemType(uri)) {
            case TcmUtils.COMPONENT_ITEM_TYPE:
                resolver = isBinary ? _componentBinaryResolver() : _componentBinaryResolver();
                break;
            case TcmUtils.PAGE_ITEM_TYPE:
                resolver = resolvePage();
                break;
            default:
                log.warn("Could not resolve link: {}", uri);
                return "";
        }

        ResolvingData resolvingData = ResolvingData.of(
                publicationId == 0 ? TcmUtils.getPublicationId(uri) : publicationId,
                TcmUtils.getItemId(uri), uri, pageId);

        return resolver.apply(resolvingData).orElse("");
    }

    private Function<ResolvingData, Optional<String>> _componentBinaryResolver() {
        return resolvingData -> {
            Optional<String> binary = resolveBinary().apply(resolvingData);
            return binary.isPresent() ? binary : resolveComponent().apply(resolvingData);
        };
    }

    protected abstract Function<ResolvingData, Optional<String>> resolveComponent();

    protected abstract Function<ResolvingData, Optional<String>> resolvePage();

    protected abstract Function<ResolvingData, Optional<String>> resolveBinary();

    @AllArgsConstructor(staticName = "of")
    @Getter
    protected static class ResolvingData {

        private int publicationId;

        private int itemId;

        private String uri;

        private int pageId;
    }
}
