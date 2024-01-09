package com.sdl.dxa.dd4t.providers;

import com.sdl.dxa.tridion.modelservice.ModelServiceClient;
import com.sdl.dxa.tridion.modelservice.ModelServiceClientConfiguration;
import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.tridion.meta.PageMeta;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.slf4j.Logger;

import jakarta.annotation.Resource;

import static org.slf4j.LoggerFactory.getLogger;
import static org.springframework.web.util.UriComponentsBuilder.fromUriString;

@SuppressWarnings("SpringAutowiredFieldsWarningInspection")
public class ModelServicePageProvider extends BrokerPageProvider implements PageProvider {

    private static final Logger log = getLogger(ModelServicePageProvider.class);

    @Resource
    private ModelServiceClient modelServiceClient;

    @Resource
    private ModelServiceClientConfiguration modelServiceClientConfiguration;

    public void setModelServiceClient(ModelServiceClient modelServiceClient) {
        this.modelServiceClient = modelServiceClient;
    }

    public void setModelServiceClientConfiguration(ModelServiceClientConfiguration modelServiceClientConfiguration) {
        this.modelServiceClientConfiguration = modelServiceClientConfiguration;
    }

    @Override
    public String getPageContentById(int id, int publication) throws ItemNotFoundException, SerializationException {
        PageMeta pageMeta = getPageMetaById(id, publication);
        return getPageContentByURL(pageMeta.getURLPath(), publication);
    }

    @Override
    public String getPageContentByURL(String url, int publication) throws ItemNotFoundException, SerializationException {
        try {
            String serviceUrl = fromUriString(modelServiceClientConfiguration.getPageModelUrl())
                    .queryParam("modelType", "DD4T")
                    .build().toUriString();

            log.debug("Loading content from Model Service {} for url = {} and publication = {}", serviceUrl, url, publication);
            return decodeAndDecompressContent(modelServiceClient.getForType(serviceUrl, String.class, "tcm", publication, url, "INCLUDE"));
        } catch (ItemNotFoundInModelServiceException e) {
            throw new ItemNotFoundException("Item for url = '" + url + "' and publication = '" + +publication + "' is not found in the Model Service", e);
        }
    }



}
