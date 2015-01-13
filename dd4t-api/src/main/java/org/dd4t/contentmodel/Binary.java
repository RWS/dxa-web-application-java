package org.dd4t.contentmodel;

/**
 * Interface for a binary items i.e. images and pdfs.
 *
 * Bear in mind that this interface is only valid for fetching actual
 * binaries through the BinaryFactory and a Binary Controller.
 *
 * This class is NOT for deserializing Multimedia Components!
 *
 *
 * @see org.dd4t.contentmodel.Multimedia
 * @author bjornl
 */
public interface Binary extends RepositoryLocalItem {

	void setBinaryData (BinaryData binaryData);
	BinaryData getBinaryData();

	void setMimeType(String mimeType);
	String getMimeType();

	void setUrlPath(String urlPath);
	String getUrlPath();

}
