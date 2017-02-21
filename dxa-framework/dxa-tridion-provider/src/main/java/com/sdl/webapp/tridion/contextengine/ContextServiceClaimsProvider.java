package com.sdl.webapp.tridion.contextengine;

import com.google.common.base.Strings;
import com.sdl.context.api.Aspect;
import com.sdl.context.api.ContextMap;
import com.sdl.context.api.exception.ResolverException;
import com.sdl.context.api.resolution.Evidence;
import com.sdl.context.api.resolution.EvidenceBuilder;
import com.sdl.context.odata.client.api.ODataContextEngine;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Component
@Slf4j
public class ContextServiceClaimsProvider implements ContextClaimsProvider {

    private static final String CONTEXT_COOKIE_NAME = "context";

    @Value("${dxa.context.service.publication.id:false}")
    private boolean isPublicationIdExpected;

    //todo dxa2 replace with bean initialization based on negated Spring profile
    private ODataContextEngine oDataContextEngine;

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private HttpServletRequest request;

    private static Map<String, Object> getClaimsMap(ContextMap<? extends Aspect> contextMap, String aspectName) {

        log.trace("#getClaimsMap(ContextMap<? extends Aspect> {}, String {})", contextMap, aspectName);

        if (contextMap == null) {
            log.warn("contextMap is null for aspect {}, returning empty claims map", aspectName);
            return Collections.emptyMap();
        }

        if (Strings.isNullOrEmpty(aspectName)) {
            Set<String> keySet = contextMap.keySet();
            Map<String, Object> result = new HashMap<>(keySet.size());
            for (String key : keySet) {
                if (!Strings.isNullOrEmpty(key)) {
                    result.putAll(getClaimsMap(contextMap, key));
                }
            }

            return result;
        } else {
            return getClaimsForAspect(contextMap, aspectName);
        }
    }

    private static Map<String, Object> getClaimsForAspect(ContextMap<? extends Aspect> contextMap, String aspectName) {
        Aspect aspect = contextMap.get(aspectName);
        Map<String, Object> result = new HashMap<>(aspect.size());
        for (String key : aspect.keySet()) {
            result.put(String.format("%s.%s", aspectName, key), aspect.get(key));
        }
        return result;
    }

    @Override
    public Map<String, Object> getContextClaims(String aspectName) throws DxaException {
        EvidenceBuilder evidenceBuilder = new EvidenceBuilder()
                .with("user-agent", request.getHeader("user-agent"));

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (CONTEXT_COOKIE_NAME.equals(cookie.getName())) {
                    evidenceBuilder.with("cookie", CONTEXT_COOKIE_NAME + '=' + cookie.getValue());
                }
            }
        }

        if (isPublicationIdExpected) {
            Localization localization = webRequestContext.getLocalization();
            evidenceBuilder.withPublicationId(Integer.valueOf(localization.getId()));
        }

        ContextMap<? extends Aspect> contextMap;
        try {
            Evidence evidence = evidenceBuilder.build();
            if (oDataContextEngine == null) {
                oDataContextEngine = new ODataContextEngine();
            }
            contextMap = oDataContextEngine.resolve(evidence);
            log.trace("Current data context engine impl is {}", oDataContextEngine.getClass());
            log.debug("Requested context map for aspect {} with evidence {}, and got {}", aspectName, evidence, contextMap);
        } catch (ResolverException e) {
            throw new DxaException("An error occurred while resolving evidence using the Context Service.", e);
        }

        return getClaimsMap(contextMap, aspectName);
    }

    @Override
    public String getDeviceFamily() {
        return null;
    }
}
