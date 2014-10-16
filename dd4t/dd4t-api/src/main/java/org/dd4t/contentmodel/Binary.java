package org.dd4t.contentmodel;

import org.joda.time.DateTime;

/**
 * Interface for a binary items i.e. images and pdfs.
 *
 * @author bjornl
 */
public interface Binary extends RepositoryLocalItem {

    /**
     * Get the schema used by the binary
     *
     * @return the schema
     */
    public Schema getSchema();

    /**
     * Set the schema
     *
     * @param schema
     */
    public void setSchema(Schema schema);

    /**
     * Return the binary data
     *
     * @return the tridion native binary data
     */
    public BinaryData getBinaryData();

    /**
     * Set the binary data
     *
     * @param binaryData
     */
    public void setBinaryData(BinaryData binaryData);

    /**
     * Get the height of the binary
     *
     * @return the height
     */
    public int getHeight();

    /**
     * Set the height of the binary
     *
     * @param height
     */
    public void setHeight(int height);

    /**
     * Get the width of the binary
     *
     * @return
     */
    public int getWidth();

    /**
     * Set the width of the binary
     *
     * @param width
     */
    public void setWidth(int width);

    /**
     * Get the alt text of the binary
     *
     * @return the alt text
     */
    public String getAlt();

    /**
     * Set the alt text
     *
     * @param alt
     */
    public void setAlt(String alt);

    /**
     * Get the mime type of the binary
     */
    public String getMimeType();

    /**
     * Set the mime type of the binary
     *
     * @param mimeType (String)
     */
    public void setMimeType(String mimeType);

    /**
     * Get the Date the binary was last published
     */
    public DateTime getLastPublishDate();

    /**
     * Set the Date the binary was last published
     *
     * @param date (Date)
     */
    public void setLastPublishDate(DateTime date);
}
