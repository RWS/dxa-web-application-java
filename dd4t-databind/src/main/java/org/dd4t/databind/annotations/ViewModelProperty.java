package org.dd4t.databind.annotations;

import org.dd4t.contentmodel.FieldType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * test
 *
 * @author R. Kempees
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ViewModelProperty {
	/*
	Entity field name in essence is the Tridion component field name
	 */
	String entityFieldName() default "";

	// TODO: Tridion Field Types should be read straight
	FieldType tridionFieldType() default FieldType.TEXT;
	boolean isMetadata() default false;
}
