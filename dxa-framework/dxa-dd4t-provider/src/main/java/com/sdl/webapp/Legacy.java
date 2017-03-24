package com.sdl.webapp;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation/shortcut for Legacy pipeline profile. This annotation should be put on {@link Component}s and subclasses
 * to conditionally enable them in case of presence of {@code legacy.provider} Spring profile.
 * <p><strong>Pay attention that this also makes a bean {@code @Primary}.</strong> This means that in case you have 2+
 * beans of the same time, this potentially won't work, solution is simple (without double use of {@code @Primary}):</p>
 * <pre><code>
 * interface Interface {}
 *
 * interface MoreConcreteInterface extends Interface {}
 *
 * &#64;Profile("!special")
 * &#64;Service
 * class Bean1_NotSpecial implements MoreConcreteInterface { }
 *
 * &#64;Profile("special")
 * &#64;Service
 * class Bean2_Special implements MoreConcreteInterface { }
 *
 * &#64;Primary
 * &#64;Profile("special")
 * &#64;Service
 * class Bean3_Special implements interface Interface {
 *      &#64;Autowired MoreConcreteInterface dependency;
 * }
 * </code></pre>
 *
 * @see Primary
 * @see Profile
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Profile("legacy.provider")
@Primary
public @interface Legacy {

}
