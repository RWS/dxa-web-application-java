package org.dd4t.core.databind;

import java.util.List;
import java.util.Map;

/**
 *
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
	// TODO: move to TridionViewModel
	boolean setGenericComponentOnComponentPresentation();
	boolean setRawDataOnModel ();
	void setRawData(Object data);
	String getRawDataAsString();
	Map<String,Object> getModelProperties();
}
