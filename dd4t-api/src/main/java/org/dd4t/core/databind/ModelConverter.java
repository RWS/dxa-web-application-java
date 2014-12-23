package org.dd4t.core.databind;

import org.dd4t.core.exceptions.SerializationException;

/**
 * test
 *
 * @author R. Kempees
 * @since 19/11/14.
 */
public interface ModelConverter {
	<T extends BaseViewModel> T convertSource(Object data, T model) throws SerializationException;
}
