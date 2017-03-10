package com.sdl.dxa.tridion.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.R2;
import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.PageNotFoundException;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResponseExtractor;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;

import static java.nio.charset.Charset.defaultCharset;

@R2
@Slf4j
@Service
public class ModelService {

    @Value("#{'${dxa.model.service.url}' + '${dxa.model.service.url.page.model}'}")
    private String pageModelUrl;

    @Value("#{'${dxa.model.service.url}' + '${dxa.model.service.url.page.source}'}")
    private String pageSourceUrl;

    @Value("#{'${dxa.model.service.url}' + '${dxa.model.service.url.entity.model}'}")
    private String entityModelUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    @Qualifier("dxaR2ObjectMapper")
    private ObjectMapper objectMapper;

    @Cacheable(value = "default", key = "'pmd' + #pageUrl")
    public PageModelData loadPageModel(String pageUrl) throws ContentProviderException {
        return _loadPage(pageModelUrl, pageUrl, _defaultExtractor(PageModelData.class));
    }

    private <T> T _loadPage(String serviceUrl, String pageUrl, ResponseExtractor<T> extractor) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();
        try {
            T page = _processRequest(serviceUrl, extractor, "tcm", localization.getId(), pageUrl);
            log.trace("Loaded '{}' for localization '{}' and page url '{}'", page, localization, pageUrl);
            return page;
        } catch (DxaItemNotFoundException e) {
            throw new PageNotFoundException("Cannot load page '" + pageUrl + "'", e);
        }
    }

    private <T> ResponseExtractor<T> _defaultExtractor(Class<T> type) {
        return response -> objectMapper.readValue(response.getBody(), type);
    }

    private <T> T _processRequest(String serviceUrl, ResponseExtractor<T> extractor, Object... params) throws ContentProviderException {
        try {
            return restTemplate.execute(serviceUrl, HttpMethod.GET, null, extractor, params);
        } catch (HttpStatusCodeException e) {
            HttpStatus statusCode = e.getStatusCode();
            log.info("Got response with a status code {}", statusCode);
            if (statusCode.is4xxClientError()) {
                throw new DxaItemNotFoundException("Item not found requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
            }
            throw new ContentProviderException("Internal server error requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
        }
    }

    @Cacheable(value = "default", key = "'emd' + #entityId")
    public EntityModelData loadEntityModel(String entityId) throws ContentProviderException {
        Localization localization = webRequestContext.getLocalization();
        EntityModelData modelData = _processRequest(entityModelUrl, _defaultExtractor(EntityModelData.class), "tcm", localization.getId(), entityId);
        log.trace("Loaded '{}' for entityId '{}'", modelData, entityId);
        return modelData;
    }

    @Cacheable(value = "default", key = "'page' + #pageUrl")
    public String loadPageSource(String pageUrl) throws ContentProviderException {
        return _loadPage(pageSourceUrl, pageUrl, response -> StreamUtils.copyToString(response.getBody(), defaultCharset()));
    }

}
