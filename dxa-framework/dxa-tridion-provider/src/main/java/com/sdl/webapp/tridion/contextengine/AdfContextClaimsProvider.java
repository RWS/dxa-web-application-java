package com.sdl.webapp.tridion.contextengine;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Common shared functionality for all ADF providers.
 */
@Slf4j
@Profile("adf.context.provider")
@Primary
@Component
public class AdfContextClaimsProvider implements ContextClaimsProvider {

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

        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeviceFamily() {
        return null;
    }

    protected Map<URI, Object> getCurrentClaims() {
        ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
        return currentClaimStore == null ? Collections.emptyMap() : currentClaimStore.getAll();
    }
}
