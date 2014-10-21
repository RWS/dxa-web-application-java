package org.dd4t.contentmodel;

import org.joda.time.DateTime;

public interface PageTemplate extends Item, HasMetadata {

	/**
	 * Get the file extension
	 *
	 * @return the file extension
	 */
	public String getFileExtension ();

	/**
	 * Set the file extension
	 *
	 * @param fileExtension
	 */
	public void setFileExtension (String fileExtension);

	public DateTime getRevisionDate ();

	public void setRevisionDate (DateTime date);
}
