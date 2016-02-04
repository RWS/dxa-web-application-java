package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import com.sdl.webapp.common.exceptions.DxaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
/**
 * <p>ContextEngineImpl class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
@Scope(value = "request")
public class ContextEngineImpl implements ContextEngine {
    private static final Logger LOG = LoggerFactory.getLogger(ContextEngineImpl.class);

    private Map<String, Object> claims;
    private Map<Class, ContextClaims> stronglyTypedClaims = new HashMap<>();
    private String deviceFamily;

    @Autowired
    private ContextClaimsProvider provider;

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends ContextClaims> T getClaims(Class<T> cls) {

        ContextClaims retval;

        try {
            if (this.claims == null) {
                this.claims = provider.getContextClaims(null);
            }

            retval = cls.newInstance();
            if (!this.stronglyTypedClaims.containsKey(cls)) {
                retval.setClaims(this.claims);
                this.stronglyTypedClaims.put(cls, retval);
            } else {
                retval = this.stronglyTypedClaims.get(cls);
            }
            return (T) retval;
        } catch (InstantiationException | IllegalAccessException | DxaException e) {
            LOG.error("Exception during getClaims()", e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDeviceFamily() {
        if (this.deviceFamily != null) {
            return this.deviceFamily;
        }

        this.deviceFamily = provider.getDeviceFamily();
        if (this.deviceFamily == null) {
            // Defaults
            DeviceClaims device = this.getClaims(DeviceClaims.class);
            if (!device.getIsMobile() && !device.getIsTablet()) this.deviceFamily = "desktop";
            if (device.getIsTablet()) this.deviceFamily = "tablet";
            if (device.getIsMobile() && !device.getIsTablet()) {
                this.deviceFamily = device.getDisplayWidth() > 319 ? "smartphone" : "featurephone";
            }
        }
        return this.deviceFamily;
    }
}
