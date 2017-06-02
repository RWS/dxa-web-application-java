package com.sdl.dxa.tridion.modelservice;

import com.sdl.webapp.common.controller.exception.InternalServerErrorException;
import com.sdl.webapp.common.exceptions.DxaItemNotFoundException;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import com.tridion.ambientdata.web.WebClaims;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

@Slf4j
@Service
public class ModelServiceClient {

    private static final String X_PREVIEW_SESSION_TOKEN = "x-preview-session-token";

    private static final String PREVIEW_SESSION_TOKEN = "preview-session-token";

    private final RestTemplate restTemplate;

    private final ModelServiceConfiguration configuration;

    @Autowired
    public ModelServiceClient(RestTemplate restTemplate,
                              ModelServiceConfiguration configuration) {
        this.restTemplate = restTemplate;
        this.configuration = configuration;
    }

    public <T> T getForType(String serviceUrl, Class<T> type, Object... params) throws DxaItemNotFoundException {
        try {
            HttpHeaders headers = new HttpHeaders();

            processPreviewToken(headers);
            processAccessToken(headers);

            ResponseEntity<T> response = restTemplate.exchange(serviceUrl, HttpMethod.GET, new HttpEntity<>(headers), type, params);
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            HttpStatus statusCode = e.getStatusCode();
            log.info("Got response with a status code {}", statusCode);
            if (statusCode.is4xxClientError()) {
                throw new DxaItemNotFoundException("Item not found requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
            }
            throw new InternalServerErrorException("Internal server error requesting '" + serviceUrl + "' with params '" + Arrays.toString(params) + "'", e);
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

    private void processAccessToken(HttpHeaders headers) {
        if (configuration.getOAuthTokenProvider() != null) {
            log.trace("Request is secured, adding security token");
            headers.add("Authorization", "Bearer" + configuration.getOAuthTokenProvider().getToken());
        }
    }


    @NotNull
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
