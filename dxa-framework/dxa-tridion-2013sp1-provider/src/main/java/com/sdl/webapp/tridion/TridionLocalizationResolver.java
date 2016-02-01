package com.sdl.webapp.tridion;

import com.tridion.dynamiccontent.DynamicContent;
import com.tridion.dynamiccontent.publication.PublicationMapping;
import org.springframework.stereotype.Component;

@Component
public class TridionLocalizationResolver extends AbstractTridionLocalizationResolver {
    @Override
    protected PublicationMappingData getPublicationMappingData(String url) throws PublicationMappingNotFoundException {
        PublicationMapping publicationMapping = DynamicContent.getInstance().getMappingsResolver().getPublicationMappingFromUrl(url);

        return new PublicationMappingData(
                String.valueOf(publicationMapping.getPublicationId()),
                getPublicationMappingPath(publicationMapping.getPath()));
    }
}
