package com.sdl.webapp.tridion.contextengine;

import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Component
@Slf4j
public class AdfContextClaimsProvider extends AbstractAdfContextClaimsProvider {

    @Override
    protected Map<URI, Object> getCurrentClaims() {
        ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
        Map<URI, Object> result = currentClaimStore == null ? Collections.<URI, Object>emptyMap() : currentClaimStore.getAll();

        logAllClaims(result);

        return result;
    }

    @Override
    protected String getClaimValueForURI(URI uri) {
        ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
        return currentClaimStore == null ? null : currentClaimStore.get(uri, String.class);
    }
}
