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

import java.util.Optional;
import java.util.function.Function;

@Slf4j
public abstract class AbstractLinkResolver implements LinkResolver {

    /**
     * @deprecated since 2.0, use {@link PathUtils#getDefaultPageName()}
     */
    @Deprecated
    public static final String DEFAULT_PAGE_NAME = PathUtils.getDefaultPageName();

    /**
     * @deprecated since 2.0, use {@link PathUtils#getDefaultPageExtension()}
     */
    @Deprecated
    public static final String DEFAULT_PAGE_EXTENSION = PathUtils.getDefaultPageExtension();

    @Value("${dxa.web.link-resolver.remove-extension:#{true}}")
    private boolean shouldRemoveExtension;

    @Value("${dxa.web.link-resolver.strip-index-path:#{true}}")
    private boolean shouldStripIndexPath;

    @Override
    @Cacheable(value = "defaultCache", key = "{ #root.methodName,  #url, #localizationId, #resolveToBinary }")
    public String resolveLink(@Nullable String url, @Nullable String localizationId, boolean resolveToBinary) {
        final int publicationId = !Strings.isNullOrEmpty(localizationId) ? Integer.parseInt(localizationId) : 0;

        String resolvedLink = _resolveLink(url, publicationId, resolveToBinary);
        String resolvedUrl = shouldStripIndexPath ? PathUtils.stripIndexPath(resolvedLink) : resolvedLink;
        return shouldRemoveExtension ? PathUtils.stripDefaultExtension(resolvedUrl) : resolvedUrl;
    }

    @Contract("null, _, _ -> null; !null, _, _ -> !null")
    private String _resolveLink(String uri, int publicationId, boolean isBinary) {
        if (uri == null || !TcmUtils.isTcmUri(uri)) {
            return uri;
        }

        Function<ResolvingData, Optional<String>> resolver;
        switch (TcmUtils.getItemType(uri)) {
            case TcmUtils.COMPONENT_ITEM_TYPE:
                resolver = isBinary ? _componentBinaryResolver() : _componentResolver();
                break;
            case TcmUtils.PAGE_ITEM_TYPE:
                resolver = _pageResolver();
                break;
            default:
                log.warn("Could not resolve link: {}", uri);
                return "";
        }

        ResolvingData resolvingData = ResolvingData.of(
                publicationId == 0 ? TcmUtils.getPublicationId(uri) : publicationId,
                TcmUtils.getItemId(uri), uri);

        return resolver.apply(resolvingData).orElse("");
    }

    private Function<ResolvingData, Optional<String>> _componentBinaryResolver() {
        return resolvingData -> {
            Optional<String> binary = _binaryResolver().apply(resolvingData);
            return binary.isPresent() ? binary : _componentResolver().apply(resolvingData);
        };
    }

    protected abstract Function<ResolvingData, Optional<String>> _componentResolver();

    protected abstract Function<ResolvingData, Optional<String>> _pageResolver();

    protected abstract Function<ResolvingData, Optional<String>> _binaryResolver();

    @AllArgsConstructor(staticName = "of")
    @Getter
    protected static class ResolvingData {

        private int publicationId;

        private int itemId;

        private String uri;
    }
}
