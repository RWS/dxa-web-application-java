package org.dd4t.core.serializers.impl.json;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.jsontype.TypeIdResolver;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.databind.util.ClassUtil;
import org.dd4t.contentmodel.FieldType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * Resolves Tridion Field Types to their proper concrete
 * classes, which are subclasses of the abstract BaseField class.
 * <p/>
 * This is done based on the "FieldType" parameter which
 * is sent by the DD4T templating assembly for each serialized Tridion Field.
 *
 * @author R. Kempees
 * @see org.dd4t.contentmodel.impl.BaseField
 * @since 04.06.2014
 */
public class TridionJsonFieldTypeIdResolver implements TypeIdResolver {

    private static final Logger LOG = LoggerFactory.getLogger(TridionJsonFieldTypeIdResolver.class);
    private static final Map<FieldType, String> fieldTypes = new HashMap<>();
    // TODO: make configurable
    private static final String NAMESPACE_PREFIX = "org.dd4t.contentmodel.impl.";

    static {
        fieldTypes.put(FieldType.Text, NAMESPACE_PREFIX + "TextField");
        fieldTypes.put(FieldType.MultiLineText, NAMESPACE_PREFIX + "TextField");
        fieldTypes.put(FieldType.Xhtml, NAMESPACE_PREFIX + "XhtmlField");
        fieldTypes.put(FieldType.Keyword, NAMESPACE_PREFIX + "KeywordField");
        fieldTypes.put(FieldType.Embedded, NAMESPACE_PREFIX + "EmbeddedField");
        // TODO: This (5) is actually a MM Link field, but the links are needed for multi values and meta
        fieldTypes.put(FieldType.MultimediaLink, NAMESPACE_PREFIX + "ComponentLinkField");
        fieldTypes.put(FieldType.ComponentLink, NAMESPACE_PREFIX + "ComponentLinkField");
        fieldTypes.put(FieldType.ExternalLink, NAMESPACE_PREFIX + "TextField");
        fieldTypes.put(FieldType.Number, NAMESPACE_PREFIX + "NumericField");
        fieldTypes.put(FieldType.Date, NAMESPACE_PREFIX + "DateField");
        fieldTypes.put(FieldType.Unknown, NAMESPACE_PREFIX + "BaseField");
    }

    private JavaType mBaseType;

    public TridionJsonFieldTypeIdResolver() {
    }

    @Override
    public void init(final JavaType javaType) {
        LOG.info("Instantiating TridionJsonFieldTypeResolver for " + javaType);
        mBaseType = javaType;
    }

    @Override
    public String idFromValue(final Object o) {
        return idFromValueAndType(o, o.getClass());
    }

    @Override
    public String idFromValueAndType(final Object o, final Class<?> aClass) {
        String name = aClass.getName();

        if (null == o) {
            return "-1";
        }

        return getIdFromClass(name);
    }

    @Override
    public String idFromBaseType() {
        return "-1";
    }

    @Override
    public JavaType typeFromId(final String s) {
        String clazzName = getClassForKey(s);
        Class<?> clazz;

        try {
            LOG.trace("Loading a '{}'", clazzName);
            clazz = ClassUtil.findClass(clazzName);
        } catch (ClassNotFoundException e) {
	        LOG.error("Could not find the class!",e);
            throw new IllegalStateException("Cannot find class '" + clazzName + "'");
        }

        return TypeFactory.defaultInstance().constructSpecializedType(mBaseType, clazz);
    }

    @Override
    public JsonTypeInfo.Id getMechanism() {
        return JsonTypeInfo.Id.CUSTOM;
    }

    private String getClassForKey(String type) {
        LOG.trace("Fetching field type for {}", type);
        FieldType fieldType = FieldType.findByName(type);

        String result = fieldTypes.get(fieldType);
        LOG.trace("Returning field type {}", result);

        return result;
    }

    private String getIdFromClass(String aClassName) {
        if (!aClassName.startsWith(NAMESPACE_PREFIX)) {
            aClassName = NAMESPACE_PREFIX + aClassName;
        }

        for (Map.Entry<FieldType, String> entry : fieldTypes.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(aClassName)) {
                LOG.trace("Found {}::{}", entry.getKey(), entry.getValue());
                return entry.getKey().toString();
            }
        }

        return "-1";
    }
}
