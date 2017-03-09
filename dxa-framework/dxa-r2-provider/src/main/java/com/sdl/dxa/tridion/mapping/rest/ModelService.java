package com.sdl.dxa.tridion.mapping.rest;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.api.datamodel.model.ViewModelData;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

@Slf4j
@Service
public class ModelService {

    @Value("#{'${dxa.model.service.url}' + '${dxa.model.service.url.page}'}")
    private String pageServiceUrl;

    @Value("#{'${dxa.model.service.url}' + '${dxa.model.service.url.entity}'}")
    private String entityServiceUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebRequestContext webRequestContext;

    @Cacheable(value = "default")
    public PageModelData loadPage(String pageUrl) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();

        try {
            PageModelData modelData = _processRequest(pageServiceUrl, PageModelData.class, "tcm", localization.getId(), pageUrl);

            log.trace("Loaded '{}' for localization '{}' and page url '{}'", modelData, localization, pageUrl);
            return modelData;
        } catch (DxaItemNotFoundException e) {
            throw new PageNotFoundException("Cannot load page '" + pageUrl + "'", e);
        }
    }

    private <T extends ViewModelData> T _processRequest(String serviceUrl, Class<T> type, Object... params) throws ContentProviderException {
        try {
            return restTemplate.getForObject(serviceUrl, type, params);
        } catch (HttpStatusCodeException e) {
            HttpStatus statusCode = e.getStatusCode();
            log.info("Got response with a status code {}", statusCode);
            if (statusCode.is4xxClientError()) {
                throw new DxaItemNotFoundException("Item not found requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
            }
            throw new ContentProviderException("Internal server error requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
        }
    }

    @Cacheable(value = "default")
    public EntityModelData loadEntity(String entityId) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();
        EntityModelData modelData = _processRequest(entityServiceUrl, EntityModelData.class, "tcm", localization.getId(), entityId);
        log.trace("Loaded '{}' for entityId '{}'", modelData, entityId);
        return modelData;
    }
}
