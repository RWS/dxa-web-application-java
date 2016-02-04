package com.sdl.webapp.common.api.contextengine;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * <p>Abstract ContextClaims class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public abstract class ContextClaims {
    private Map<String, Object> claims;

    private static <T> T castValue(Object value, Class<T> cls) {
        if (value == null || cls.isInstance(value)) {
            return (T) value;
        }

        if (cls == String.class) {
            return (T) value.toString();
        }


        return cls.cast(value);
    }

    /**
     * <p>Setter for the field <code>claims</code>.</p>
     *
     * @param claims a {@link java.util.Map} object.
     */
    public void setClaims(Map<String, Object> claims) {
        this.claims = claims;
    }

    /**
     * <p>getAspectName.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    protected abstract String getAspectName();

    /**
     * <p>getClaimName.</p>
     *
     * @param properyName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected String getClaimName(String properyName) {
        return String.format("%s.%s", getAspectName(), properyName);
    }

    /**
     * <p>getClaimValue.</p>
     *
     * @param propertyName a {@link java.lang.String} object.
     * @param cls          a {@link java.lang.Class} object.
     * @param <T>          a T object.
     * @return a T object.
     */
    public <T> T getClaimValue(String propertyName, Class<T> cls) {
        Object claimValue;
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            return castValue(this.claims.get(claimName), cls);
        } else {
            return null;
        }
    }

    /**
     * <p>getClaimValues.</p>
     *
     * @param propertyName a {@link java.lang.String} object.
     * @param cls          a {@link java.lang.Class} object.
     * @param <T>          a T object.
     * @return a {@link java.util.ArrayList} object.
     */
    public <T> ArrayList<T> getClaimValues(String propertyName, Class<T> cls) {
        Object claimValue;
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            claimValue = this.claims.get(claimName);
            if (claimValue == null) {
                return null;
            } else {
                if (Set.class.isInstance(claimValue)) {
                    Set retval = ((Set) claimValue);
                    ArrayList<T> array = new ArrayList<>();
                    for (Object o : retval) {
                        array.add(castValue(o, cls));
                    }
                    return array;
                } else {
                    return (ArrayList<T>) claimValue;
                }
            }
        }
        return null;
    }
}
