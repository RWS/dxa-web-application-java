package org.dd4t.databind.serializers.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
@JsonTypeInfo (
		use = JsonTypeInfo.Id.CUSTOM,
		include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
		property = "FieldType",
		visible = true)
@JsonTypeIdResolver (TridionJsonFieldTypeIdResolver.class)
public class BaseFieldMixIn {
}
