package com.sdl.webapp.common.api.contextengine;

import java.net.URI;
import java.util.Map;

public abstract class ContextClaims {
    private Map<String, Object> claims;

    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    protected abstract String getAspectName();


    protected String getClaimName(String properyName) {
        return String.format("%s.%s", getAspectName(), properyName);
    }


    public <T> T getClaimValue(String propertyName, Class<T> cls) {
        Object claimValue;
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            return castValue(this.claims.get(claimName), cls);
        } else {
            return null;
        }
        //this.claims.TryGetValue(getClaimName(propertyName), out claimValue);
        //return CastValue<T>(claimValue);
    }

    public <T> T[] getClaimValues(String propertyName, Class<T> cls) {
        Object claimValue;
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            claimValue = this.claims.get(propertyName);
            if (claimValue == null) {
                return null;
            } else {

            }


        }
        {
            return null;
        }
         /*
         this.claims.TryGetValue(getClaimName(propertyName), out claimValue);
         return (claimValue == null) ? null : (from object item in (IEnumerable) claimValue select CastValue<T>(item)).ToArray();*/
    }

    private static <T> T castValue(Object value, Class<T> cls) {
        if (value == null || cls.isInstance(value)) {
            return (T) value;
        }

        if (cls == String.class) {
            return (T) (Object) value.toString();
        }


        return cls.cast(value);
    }
}
