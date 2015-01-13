package org.dd4t.contentmodel.impl;

import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.BinaryData;

public class BinaryImpl extends BaseRepositoryLocalItem implements Binary {

	private BinaryData binaryData;
	private String mimeType;
	private String urlPath;

	@Override public void setBinaryData (final BinaryData binaryData) {
		this.binaryData = binaryData;
	}

	@Override public BinaryData getBinaryData () {
		return this.binaryData;
	}

	@Override public void setMimeType (final String mimeType) {
		this.mimeType = mimeType;
	}

	@Override public String getMimeType () {
		return this.mimeType;
	}

	@Override public void setUrlPath (final String urlPath) {
		this.urlPath = urlPath;
	}

	@Override public String getUrlPath () {
		return this.urlPath;
	}
}