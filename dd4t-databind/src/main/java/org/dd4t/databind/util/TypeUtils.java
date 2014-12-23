package org.dd4t.databind.util;

import org.dd4t.core.databind.BaseViewModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class TypeUtils {
	private static final Logger LOG = LoggerFactory.getLogger(TypeUtils.class);

	private TypeUtils() {

	}

	public static Type getRuntimeTypeOfTypeParameter (Type type) {
		Type[] genericTypes = ((ParameterizedType)type).getActualTypeArguments();
		if (genericTypes != null && genericTypes.length > 0) {
			LOG.debug("Type was List. Runtime Parametrized type is: {}" ,genericTypes[0].toString());
			return genericTypes[0];
		}
		return Object.class;
	}

	public static boolean classIsViewModel(Class<?> clazz) {
		if (BaseViewModel.class.isAssignableFrom(clazz)) {
			LOG.debug("Current class is a View Model.");
			return true;
		}
		return false;
	}
}
