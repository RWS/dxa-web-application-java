package com.sdl.webapp.common.api.contextengine;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.NumberUtils.convertNumberToTargetClass;

/**
 * Common functionality for context claims.
 */
@Slf4j
public abstract class ContextClaims {

    @Setter
    @Getter(PROTECTED)
    private Map<String, Object> claims;

    @Nullable
    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class,
            InvocationTargetException.class, InstantiationException.class})
    private static <T> T castValue(@Nullable Object value, @NonNull Class<T> cls) {
        if (value == null) {
            log.debug("Claim value is null");
            return null;
        }

        if (cls.isInstance(value)) {
            log.debug("Claim value {} is of {} while requested {}, and does not require a cast", value, value.getClass(), cls);
            return cls.cast(value);
        }

        if (String.class.equals(cls)) {
            log.debug("String class is requested, while claim is not String, calling #toString()");
            return cls.cast(value.toString());
        }

        if (Number.class.isInstance(value) && Number.class.isAssignableFrom(cls)) {
            log.debug("Claim value {} is a number of {}, but number conversion to {} is requested", value, value.getClass(), cls);
            return cls.getConstructor(String.class).newInstance(
                    convertNumberToTargetClass(((Number) value), getRawNumberClass(cls)).toString());
        }

        log.warn("Cannot cast {} to {}", value, cls);
        return null;
    }

    private static <T> Class<? extends Number> getRawNumberClass(@NonNull Class<T> cls) {
        if (Integer.class.isAssignableFrom(cls)) {
            return Integer.class;
        }

        if (Double.class.isAssignableFrom(cls)) {
            return Double.class;
        }

        if (Short.class.isAssignableFrom(cls)) {
            return Short.class;
        }

        if (Byte.class.isAssignableFrom(cls)) {
            return Byte.class;
        }

        if (Float.class.isAssignableFrom(cls)) {
            return Float.class;
        }

        return Long.class;
    }

    /**
     * Returns a single claim value.
     *
     * @param propertyName a name of property to retrieve from claims
     * @param cls          a class to cast to
     * @param <T>          a generic expected type
     * @return a value of given type of null if not present
     * @deprecated since 1.5, use {@link #getSingleClaim(String, Class)}
     */
    @Nullable
    @Deprecated
    public <T> T getClaimValue(@NonNull String propertyName, @NonNull Class<T> cls) {
        return getSingleClaim(propertyName, cls);
    }

    /**
     * <p>Gets a list of claim values. Expects to have a list in a map of claims.</p>
     *
     * @param propertyName a name of property to retrieve from claims
     * @param cls          a class to cast each element to
     * @param <T>          a generic expected type
     * @return a list of values of given type of null if not present
     * @deprecated since 1.5
     */
    @Nullable
    @Deprecated
    public <T> ArrayList<T> getClaimValues(@NonNull String propertyName, @NonNull Class<T> cls) {
        List<T> list = getClaimsList(propertyName, cls);
        return list != null ? new ArrayList<>(list) : null;
    }

    /**
     * Gets a name of a particular aspect.
     *
     * @return a name of aspect
     */
    protected abstract String getAspectName();

    /**
     * Returns a full name of a claim with an aspect.
     *
     * @param propertyName a name of a property
     * @return a full name of a claim
     */
    //todo dxa2 will probably be private
    @NonNull
    protected String getClaimName(String propertyName) {
        return String.format("%s.%s", getAspectName(), propertyName);
    }

    @Nullable
    protected <T> T getSingleClaim(@NonNull String propertyName, @NonNull Class<T> cls) {
        return castValue(getClaimValue(propertyName), cls);
    }

    @Nullable
    protected <T> List<T> getClaimsList(@NonNull String propertyName, @NonNull final Class<T> cls) {
        Object claimValue = getClaimValue(propertyName);
        if (claimValue == null) {
            return null;
        }

        if (Collection.class.isInstance(claimValue)) {
            //noinspection unchecked
            return Lists.newArrayList(Collections2.transform((Collection<Object>) claimValue, new Function<Object, T>() {
                @Override
                public T apply(Object input) {
                    return castValue(input, cls);
                }
            }));
        }

        return null;
    }

    @Nullable
    private Object getClaimValue(@NonNull String propertyName) {
        String claimName = getClaimName(propertyName);
        log.trace("Claim name to retrieve {}", claimName);

        Map<String, Object> claims = getClaims();

        if (!claims.containsKey(claimName)) {
            log.debug("Requested claim {} is not in claims {}", claimName, claims);
            return null;
        }

        return claims.get(claimName);
    }
}
