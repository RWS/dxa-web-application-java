package org.dd4t.databind.viewmodel.base;

import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.util.TCMURI;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.Map;

/**
 * Extend your Tridion models from here for
 * XPM support and generic Tridion Item data.
 *
 * TODO: Do we want the ComponentType,
 * Categories, Schema, Title, Keywords, Version, Publication and Org Item nodes in here?
 *
 * @author R. Kempees
 */
public abstract class TridionViewModelBase extends ViewModelBase implements TridionViewModel {

	private TCMURI itemTcmUri;
	private TCMURI templateUri;
	private DateTime lastModifiedDate;
	private DateTime lastPublishDate;

	private transient Map<String, XPMInfo> fieldMap = new HashMap<String, XPMInfo>();

	@Override public TCMURI getTcmUri () {
		return this.itemTcmUri;
	}

	@Override public void setTcmUri (final TCMURI tcmUri) {
		this.itemTcmUri = tcmUri;
	}

	@Override public TCMURI getTemplateUri () {
		return this.templateUri;
	}

	@Override public void setTemplateUri (final TCMURI tcmUri) {
		this.templateUri = tcmUri;
	}

	@Override public DateTime getLastModified () {
		return this.lastModifiedDate;
	}

	@Override public void setLastModified (final DateTime lastModified) {
		this.lastModifiedDate = lastModified;
	}

	@Override public DateTime getLastPublishDate () {
		return this.lastPublishDate;
	}

	@Override public void setLastPublishDate (final DateTime lastPublishDate) {
		this.lastPublishDate = lastPublishDate;
	}

	public String getXPath(final String fieldName) {
		XPMInfo xpmInfo = fieldMap.get(fieldName);
		if (xpmInfo != null) {
			return xpmInfo.getXpath();
		} else {
			throw new IllegalArgumentException("Unknown field='" + fieldName + "' in " + getFieldMap().keySet());
		}
	}

	public void addXpmEntry(final String fieldName, final String xpath, final boolean multiValued) {
		fieldMap.put(fieldName, new XPMInfo(xpath, multiValued));
	}

	public Map<String, XPMInfo> getFieldMap() {
		return fieldMap;
	}

	public static class XPMInfo {
		private final String xpath;
		private final boolean multiValued;

		XPMInfo(final String xpath, final boolean multiValued) {
			this.xpath = xpath;
			this.multiValued = multiValued;
		}

		public String getXpath() {
			return xpath;
		}

		public boolean isMultiValued() {
			return multiValued;
		}
	}
}
