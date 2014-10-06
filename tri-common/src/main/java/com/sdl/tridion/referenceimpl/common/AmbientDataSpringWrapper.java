package com.sdl.tridion.referenceimpl.common;

import com.tridion.ambientdata.claimstore.ClaimStore;
import org.springframework.stereotype.Component;

/**
 * Wrapper to hold the Ambient Data claim store, so that it can be accessed from the Spring application context.
 */
@Component
public class AmbientDataSpringWrapper {

    private ClaimStore claimStore;

    public ClaimStore getClaimStore() {
        return claimStore;
    }

    public void setClaimStore(ClaimStore claimStore) {
        this.claimStore = claimStore;
    }
}
