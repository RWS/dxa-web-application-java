package com.sdl.webapp.tridion.contextengine;

import com.tridion.ambientdata.AmbientDataContext;
import com.tridion.ambientdata.claimstore.ClaimStore;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.Collections;
import java.util.Map;

@Component
/**
 * <p>AdfContextClaimsProvider class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@Profile("adf.context.provider")
@Primary
public class AdfContextClaimsProvider extends AbstractAdfContextClaimsProvider {

    /**
     * {@inheritDoc}
     */
    protected String getClaimValueForURI(URI uri) {
        ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
        return currentClaimStore == null ? null : currentClaimStore.get(uri, String.class);
    }

    /**
     * <p>getCurrentClaims.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    protected Map<URI, Object> getCurrentClaims() {
        ClaimStore currentClaimStore = AmbientDataContext.getCurrentClaimStore();
        return currentClaimStore == null ? Collections.<URI, Object>emptyMap() : currentClaimStore.getAll();
    }
}
