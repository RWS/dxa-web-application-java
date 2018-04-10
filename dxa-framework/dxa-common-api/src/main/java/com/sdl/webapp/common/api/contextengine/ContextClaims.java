package com.sdl.webapp.common.api.contextengine;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static lombok.AccessLevel.PROTECTED;
import static org.springframework.util.NumberUtils.convertNumberToTargetClass;

/**
 * Common functionality for context claims.
 *
 * @dxa.publicApi
 */
@Slf4j
public abstract class ContextClaims {

    /**
     * Mapping of Number classes to themselves to resolve <code>? extends Number</code> from <code>?</code> generics.
     */
    private static final ImmutableMap<Class<?>, Class<? extends Number>> RAW_NUMBER_CLASSES =
            ImmutableMap.<Class<?>, Class<? extends Number>>builder()
                    .put(Integer.class, Integer.class)
                    .put(Double.class, Double.class)
                    .put(Short.class, Short.class)
                    .put(Byte.class, Byte.class)
                    .put(Float.class, Float.class)
                    .put(Long.class, Long.class)
                    .build();

    @Setter
    @Getter(PROTECTED)
    private Map<String, Object> claims;

    /**
     * Casts given claim value to a given type if possible, trying also to convert {@link Number} to another child-class.
     *
     * @param value claim value to cast
     * @param cls   class to cast value to
     * @param <T>   generic type of expected result
     * @return casted value, or null if value is null or impossible to cast
     */
    @Nullable
    @Contract(value = "null, _ -> null", pure = true)
    @SneakyThrows({NoSuchMethodException.class, IllegalAccessException.class,
            InvocationTargetException.class, InstantiationException.class})
    public static <T> T castClaim(@Nullable Object value, @NonNull Class<T> cls) {
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
                    convertNumberToTargetClass((Number) value, RAW_NUMBER_CLASSES.get(cls)).toString());
        }

        log.warn("Cannot cast {} to {}", value, cls);
        return null;
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
    @NonNull
    protected String getClaimName(String propertyName) {
        return String.format("%s.%s", getAspectName(), propertyName);
    }

    @Nullable
    protected <T> T getSingleClaim(@NonNull String propertyName, @NonNull Class<T> cls) {
        return castClaim(getClaimValue(propertyName), cls);
    }

    @NonNull
    protected <T> List<T> getClaimsList(@NonNull String propertyName, @NonNull final Class<T> cls) {
        Object claimValue = getClaimValue(propertyName);
        if (claimValue == null) {
            return Collections.emptyList();
        }

        if (Collection.class.isInstance(claimValue)) {
            //noinspection unchecked
            return Lists.newArrayList(Collections2.transform((Collection<Object>) claimValue, new Function<Object, T>() {
                @Override
                public T apply(Object input) {
                    return castClaim(input, cls);
                }
            }));
        }

        return Collections.emptyList();
    }

    @Nullable
    private Object getClaimValue(@NonNull String propertyName) {
        String claimName = getClaimName(propertyName);
        log.trace("Claim name to retrieve {}", claimName);

        Map<String, Object> claimsMap = getClaims();

        if (!claimsMap.containsKey(claimName)) {
            log.debug("Requested claim {} is not in claims {}", claimName, claimsMap);
            return null;
        }

        return claimsMap.get(claimName);
    }
}
