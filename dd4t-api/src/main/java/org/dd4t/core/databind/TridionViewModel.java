package org.dd4t.core.databind;

import org.dd4t.core.util.TCMURI;
import org.joda.time.DateTime;

/**
 * TODO: API Change!
 *
 * @author R. Kempees
 * @since 12/11/14.
 */
public interface TridionViewModel extends BaseViewModel {

	TCMURI getTcmUri();
	void setTcmUri(TCMURI tcmUri);

	TCMURI getTemplateUri();
	void setTemplateUri (TCMURI tcmUri);

	DateTime getLastModified();
	void setLastModified(DateTime lastModified);

	DateTime getLastPublishDate();
	void setLastPublishDate(DateTime lastPublishDate);

	String getXPath(final String fieldName);
	void addXpmEntry(final String fieldName, final String xpath, final boolean multiValued);

	boolean setGenericComponentOnComponentPresentation();
}
