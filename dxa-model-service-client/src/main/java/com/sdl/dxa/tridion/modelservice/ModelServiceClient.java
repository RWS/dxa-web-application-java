package com.sdl.dxa.tridion.modelservice;

import com.sdl.dxa.tridion.modelservice.exceptions.ItemNotFoundInModelServiceException;
import com.sdl.dxa.tridion.modelservice.exceptions.ModelServiceBadRequestException;
import com.sdl.dxa.tridion.modelservice.exceptions.ModelServiceInternalServerErrorException;
import com.sdl.web.client.impl.OAuthTokenProvider;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.ambientdata.web.WebClaims;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import javax.cache.annotation.CacheResult;
import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static org.slf4j.LoggerFactory.getLogger;

@Service
public class ModelServiceClient {

    private static final Logger log = getLogger(ModelServiceClient.class);

    private static final String X_PREVIEW_SESSION_TOKEN = "x-preview-session-token";

    private static final String PREVIEW_SESSION_TOKEN = "preview-session-token";

    private final ModelServiceConfiguration configuration;

    @SuppressWarnings("SpringAutowiredFieldsWarningInspection")
    @Autowired(required = false)
    private RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public ModelServiceClient(ModelServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    @CacheResult(cacheName = "model-service",
            exceptionCacheName = "failures", cachedExceptions = {
            ItemNotFoundInModelServiceException.class,
            ModelServiceBadRequestException.class
    })
    public <T> T getForType(String serviceUrl, Class<T> type, Object... params) throws ItemNotFoundInModelServiceException {
        return _makeRequest(serviceUrl, type, false, params);
    }

    private <T> T _makeRequest(String serviceUrl, Class<T> type, boolean isRetry, Object... params) throws ItemNotFoundInModelServiceException {
        try {
            HttpHeaders headers = new HttpHeaders();

            processPreviewToken(headers);
            processAccessToken(headers, isRetry);

            ResponseEntity<T> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(headers), type, params);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            HttpStatus statusCode = e.getStatusCode();
            log.info("Got response with a status code {}", statusCode);

            if (statusCode.is4xxClientError()) {
                if (statusCode == HttpStatus.NOT_FOUND) {
                    String message = "Item not found requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'";
                    log.info(message);
                    throw new ItemNotFoundInModelServiceException(message, e);
                } else if (statusCode == HttpStatus.UNAUTHORIZED && !isRetry) {
                    log.info("Got 401 status code, reason: {}, check if token is expired and retry if so", statusCode.getReasonPhrase());
                    return _makeRequest(serviceUrl, type, true, params);
                } else {
                    String message = "Wrong request to the model service: " + serviceUrl + ", reason: " + statusCode.getReasonPhrase() + "error code: " + statusCode.value();
                    log.info(message);
                    throw new ModelServiceBadRequestException(message, e);
                }
            }
            String message = "Internal server error requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'";
            log.warn(message);
            throw new ModelServiceInternalServerErrorException(message, e);
        }
    }

    private void processPreviewToken(HttpHeaders headers) {
        //noinspection unchecked
        String previewToken = _getClaimValue(WebClaims.REQUEST_HEADERS, X_PREVIEW_SESSION_TOKEN,
                claim -> Optional.of(((List<String>) claim).get(0)))
                .orElseGet(() -> _getClaimValue(WebClaims.REQUEST_COOKIES, PREVIEW_SESSION_TOKEN,
                        claim -> Optional.of(claim.toString()))
                        .orElse(null));

        if (previewToken != null) {
            // commented because of bug in CIS https://jira.sdl.com/browse/CRQ-3935
            // headers.add(X_PREVIEW_SESSION_TOKEN, previewToken);
            headers.add(HttpHeaders.COOKIE, String.format("%s=%s", PREVIEW_SESSION_TOKEN, previewToken));
        }
    }

    private void processAccessToken(HttpHeaders headers, boolean isRetry) {
        OAuthTokenProvider authTokenProvider = configuration.getOAuthTokenProvider();
        if (authTokenProvider != null) {
            log.trace("Request is secured, adding security token, it is retry: {}", isRetry);
            headers.add("Authorization", "Bearer" + authTokenProvider.getToken());
        }
    }

    private Optional<String> _getClaimValue(URI uri, String key, Function<Object, Optional<String>> deriveValue) {
        ClaimStore claimStore = AmbientDataContext.getCurrentClaimStore();
        if (claimStore != null) {
            Map claims = claimStore.get(uri, Map.class);
            if (claims != null && claims.containsKey(key)) {
                return deriveValue.apply(claims.get(key));
            }
        }
        return Optional.empty();
    }
}
