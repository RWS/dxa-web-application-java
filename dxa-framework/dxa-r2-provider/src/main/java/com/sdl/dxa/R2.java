package com.sdl.dxa;

import com.sdl.webapp.tridion.navigation.DynamicNavigationProvider;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker annotation/shortcut for R2 pipeline profile. This annotation should be put on {@link Component}s and subclasses
 * to conditionally enable them in case of presence of {@code r2.provider} Spring profile.
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
 * @see DynamicNavigationProvider
 * @see com.sdl.dxa.tridion.navigation.StaticNavigationProvider
 * @see com.sdl.webapp.tridion.navigation.AbstractStaticNavigationProvider
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
@Profile("r2.provider")
@Primary
public @interface R2 {

}
