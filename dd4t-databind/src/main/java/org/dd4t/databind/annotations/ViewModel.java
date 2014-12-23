package org.dd4t.databind.annotations;

/**
 * test
 *
 * @author R. Kempees
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewModel {
	String[] viewModelNames() default "";
	String[] rootElementNames() default "";
	boolean setRawData () default false;
	boolean setComponentObject() default false;
}
