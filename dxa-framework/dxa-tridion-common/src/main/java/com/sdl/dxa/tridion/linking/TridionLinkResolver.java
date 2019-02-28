package com.sdl.dxa.tridion.linking;

import com.sdl.webapp.tridion.linking.AbstractLinkResolver;
import com.tridion.linking.BinaryLink;
import com.tridion.linking.ComponentLink;
import com.tridion.linking.PageLink;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Function;

/**
 * Tridion link resolver.
 *
 * @deprecated since PCA implementation added which supports mashup scenario.
 */
@Component
@Profile("cil.providers.active")
@Deprecated
public class TridionLinkResolver extends AbstractLinkResolver {

    @Override
    protected Function<ResolvingData, Optional<String>> componentResolver() {
        return resolvingData -> Optional.ofNullable(
                new ComponentLink(resolvingData.getPublicationId()).getLink(resolvingData.getItemId()).getURL());
    }

    @Override
    protected Function<ResolvingData, Optional<String>> pageResolver() {
        return resolvingData -> Optional.ofNullable(
                new PageLink(resolvingData.getPublicationId()).getLink(resolvingData.getItemId()).getURL());
    }

    @Override
    protected Function<ResolvingData, Optional<String>> binaryResolver() {
        return resolvingData -> {
            String uri = resolvingData.getUri();

            String componentURI = uri.startsWith("tcm:") ? uri : ("tcm:" + uri);
            return Optional.ofNullable(
                    new BinaryLink(resolvingData.getPublicationId())
                            .getLink(componentURI, null, null, null, false)
                            .getURL());
        };
    }
}
