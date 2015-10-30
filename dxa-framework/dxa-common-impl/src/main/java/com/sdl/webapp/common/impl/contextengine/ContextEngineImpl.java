package com.sdl.webapp.common.impl.contextengine;

import com.sdl.webapp.common.api.contextengine.ContextClaims;
import com.sdl.webapp.common.api.contextengine.ContextClaimsProvider;
import com.sdl.webapp.common.api.contextengine.ContextEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class ContextEngineImpl implements ContextEngine {

    private Map<String, Object> claims;
    private Map<Class, ContextClaims> stronglyTypedClaims = new HashMap<Class, ContextClaims>();
    private String deviceFamily;
    private ContextClaimsProvider provider;

    @Autowired
    public ContextEngineImpl(ContextClaimsProvider provider) {
        this.provider = provider;
    }

    @Override
    public <T extends ContextClaims> T getClaims(Class<T> cls) {
        ContextClaims retval;

        if (this.claims == null) {
            this.claims = provider.getContextClaims(null);
        }
        try {
            retval = cls.newInstance();
            if (!this.stronglyTypedClaims.containsKey(cls)) {
                retval.setClaims(this.claims);
                this.stronglyTypedClaims.put(cls, retval);
            } else {
                retval = this.stronglyTypedClaims.get(cls);
            }
            return (T) retval;
        } catch (InstantiationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

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