package com.sdl.webapp.tridion.contextengine;

import com.google.common.base.Strings;
import com.sdl.context.api.Aspect;
import com.sdl.context.api.ContextMap;
import com.sdl.context.api.exception.ResolverException;
import com.sdl.context.api.resolution.Evidence;
import com.sdl.context.api.resolution.EvidenceBuilder;
import com.sdl.context.odata.client.api.ODataContextEngine;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.exceptions.DxaException;
import lombok.extern.slf4j.Slf4j;
import org.dd4t.core.util.HttpUtils;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private ODataContextEngine oDataContextEngine;

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

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getContextClaims(String aspectName) throws DxaException {
        HttpServletRequest request = HttpUtils.getCurrentRequest();

        EvidenceBuilder evidenceBuilder = new EvidenceBuilder()
                .with("user-agent", request.getHeader("user-agent"));

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (CONTEXT_COOKIE_NAME.equals(cookie.getName())) {
                    evidenceBuilder.with("cookie", CONTEXT_COOKIE_NAME + '=' + cookie.getValue());
                }
            }
        }

        ContextMap<? extends Aspect> contextMap;
        try {
            Evidence evidence = evidenceBuilder.build();
            contextMap = oDataContextEngine.resolve(evidence);
            log.trace("Current data context engine impl is {}", oDataContextEngine.getClass());
            log.debug("Requested context map for aspect {} with evidence {}, and got {}", aspectName, evidence, contextMap);
        } catch (ResolverException e) {
            throw new DxaException("An error occurred while resolving evidence using the Context Service.", e);
        }

        return getClaimsMap(contextMap, aspectName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeviceFamily() {
        // TODO TSI-789: this functionality overlaps with "Context Expressions"
        return null;
    }
}
