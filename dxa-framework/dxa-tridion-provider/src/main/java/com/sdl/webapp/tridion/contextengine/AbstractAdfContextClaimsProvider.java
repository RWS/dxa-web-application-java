package com.sdl.webapp.tridion.contextengine;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Common shared functionality for all ADF providers.
 */
@Slf4j
public abstract class AbstractAdfContextClaimsProvider implements ContextClaimsProvider {

    private static final String TAF_CLAIM_CONTEXT = "taf:claim:context:";

    private static String appendAspectName(String aspectName) {
        String claimNamePrefix = TAF_CLAIM_CONTEXT;
        if (!Strings.isNullOrEmpty(aspectName)) {
            claimNamePrefix += aspectName + ':';
        }
        return claimNamePrefix;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, Object> getContextClaims(String aspectName) {
        String claimNamePrefix = appendAspectName(aspectName);

        Set<Entry<URI, Object>> entries = getCurrentClaims().entrySet();
        Map<String, Object> result = new HashMap<>(entries.size());
        for (Entry<URI, Object> claim : entries) {
            String claimName = claim.getKey().toString();
            if (!claimName.startsWith(claimNamePrefix)) {
                continue;
            }

            String propertyName = claimName.substring(TAF_CLAIM_CONTEXT.length()).replace(':', '.');
            result.put(propertyName, claim.getValue());
        }

        logAllClaims(result);

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeviceFamily() {
        return null;
    }

    protected abstract Map<URI, Object> getCurrentClaims();

    private void logAllClaims(@NotNull Map<String, Object> claims) {
        if (!log.isDebugEnabled()) {
            return;
        }

        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            log.debug("ADF Context Claim: {} <> {}", entry.getKey(), entry.getValue());
        }
    }
}
