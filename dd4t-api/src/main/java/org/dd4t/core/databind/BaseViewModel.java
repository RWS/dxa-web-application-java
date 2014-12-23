package org.dd4t.core.databind;

import java.util.HashMap;
import java.util.List;

/**
 * TODO: API Change!
 *
 * @author R. Kempees
 */
public interface BaseViewModel {
	/**
	 * Use this list to push the same model with different
	 * Viewnames on the Request stack or to do any other processing
	 *
	 * @return a List of all associated view names
	 */
	List<String> getViewNames();
	boolean setGenericComponentOnComponentPresentation();
	boolean setRawDataOnModel ();
	void setRawData(Object data);
	String getRawDataAsString();
	// TODO: move the annotation to the API?
	HashMap<String,?> getModelProperties();
}
