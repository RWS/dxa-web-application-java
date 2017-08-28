package com.sdl.dxa.dd4t.providers;

import com.sdl.dxa.common.dto.PageRequestDto;
import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceConfiguration;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.tridion.meta.PageMeta;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.impl.BrokerPageProvider;

import javax.annotation.Resource;

import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
@Slf4j
@Setter
public class ModelServicePageProvider extends BrokerPageProvider implements PageProvider {

    @Resource
    private ModelServiceClient modelServiceClient;

    @Resource
    private ModelServiceConfiguration modelServiceConfiguration;

    @Override
    public String getPageContentById(int id, int publication) throws ItemNotFoundException, SerializationException {
        PageMeta pageMeta = getPageMetaById(id, publication);
        return getPageContentByURL(pageMeta.getURLPath(), publication);
    }

    @Override
    public String getPageContentByURL(String url, int publication) throws ItemNotFoundException, SerializationException {
        PageRequestDto pageRequestDto = PageRequestDto.builder(publication, url).build();
        try {
            String serviceUrl = fromUriString(modelServiceConfiguration.getPageModelUrl())
                    .queryParam("modelType", "DD4T")
                    .build().toUriString();

            log.debug("Loading content from Model Service for url = {} and request = {}", serviceUrl, pageRequestDto);
            return decodeAndDecompressContent(modelServiceClient.getForType(serviceUrl, String.class,
                    pageRequestDto.getUriType(),
                    pageRequestDto.getPublicationId(),
                    pageRequestDto.getPath(),
                    pageRequestDto.getIncludePages()));
        } catch (DxaItemNotFoundException e) {
            throw new ItemNotFoundException("Item for request '" + pageRequestDto + "' is not found in the Model Service", e);
        }
    }
}
