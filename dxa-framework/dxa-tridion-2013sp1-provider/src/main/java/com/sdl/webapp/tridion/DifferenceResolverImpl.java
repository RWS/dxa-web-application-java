package com.sdl.webapp.tridion;

import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.stereotype.Component;

@Component
public class DifferenceResolverImpl implements DifferenceResolver {
    @Override
    public PublicationMapping getPublicationMappingFromUrl(String url) {
        return DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(url);
    }
}
