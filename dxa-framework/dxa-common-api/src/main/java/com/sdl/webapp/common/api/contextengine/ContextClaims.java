package com.sdl.webapp.common.api.contextengine;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

/**
 * <p>Abstract ContextClaims class.</p>
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
     * <p>Gets a name of aspect.</p>
     *
     * @return a name of aspect
     */
    protected abstract String getAspectName();

    /**
     * <p>getClaimName.</p>
     *
     * @param properyName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    //todo dxa2 will probably be private
    protected String getClaimName(String properyName) {
        return String.format("%s.%s", getAspectName(), properyName);
    }

    /**
     * <p>Gets a single claim value.</p>
     *
     * @param propertyName a name of property to retrieve from claims
     * @param cls a class to cast to
     * @param <T> a generic expected type
     * @return a value of given type of null if not present
     */
    //todo dxa2 merge with #getClaimValues()
    public <T> T getClaimValue(String propertyName, Class<T> cls) {
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            return castValue(this.claims.get(claimName), cls);
        } else {
            return null;
        }
    }

    /**
     * <p>Gets a list of claim values. Expects to have a list in a map of claims.</p>
     *
     * @param propertyName a name of property to retrieve from claims
     * @param cls a class to cast each element to
     * @param <T> a generic expected type
     * @return a list of values of given type of null if not present
     */
    //todo dxa2 return Collection
    public <T> ArrayList<T> getClaimValues(String propertyName, Class<T> cls) {
        String claimName = getClaimName(propertyName);
        if (this.claims.containsKey(claimName)) {
            Object claimValue = this.claims.get(claimName);
            if (claimValue == null) {
                return null;
            } else {
                if (Set.class.isInstance(claimValue)) {
                    Set retval = (Set) claimValue;
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
