package com.sdl.webapp.common.api.contextengine;

import java.net.URI;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

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
    }

    public <T> ArrayList<T> getClaimValues(String propertyName, Class<T> cls) {
        Object claimValue;
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            claimValue = this.claims.get(claimName);
            if (claimValue == null) {
                return null;
            } else {
                if(Set.class.isInstance(claimValue))
                {
                    Set retval = ((Set)claimValue);
                    ArrayList<T> array = new ArrayList<T>();
                    for(Object o : retval)
                    {
                        array.add(castValue(o, cls));
                    }
                    return array;
                }
                else
                {
                    return (ArrayList<T>)claimValue;
                }
            }
        }
        return null;
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
