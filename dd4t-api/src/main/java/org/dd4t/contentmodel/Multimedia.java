package org.dd4t.contentmodel;

/**
 * Interface for a binary items i.e. images and pdfs.
 *
 * @author bjornl
 */
public interface Multimedia {

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
     * Get the size of the binary
     *
     * @return Size in bytes
     */
    public int getSize();

    /**
     * Set the size of the binary
     *
     * @param size in bytes
     */
    public void setSize(int size);

    /**
     * Get the alt text of the binary
     *
     * @return the alt text
     */
    public String getAlt();

    /**
     * Set the al text
     *
     * @param alt
     */
    public void setAlt(String alt);

    /**
     * Get the URL of the binary
     *
     * @return the URL
     */
    public String getUrl();

    /**
     * Set the URL
     *
     * @param url
     */
    public void setUrl(String url);

    /**
     * Get the mime type of the binary
     *
     * @return the mime type
     */
    public String getMimeType();

    /**
     * Set the mime type
     *
     * @param mimeType type
     */
    public void setMimeType(String mimeType);

    /**
     * Get the file extension
     *
     * @return the file extension
     */
    public String getFileExtension();

    /**
     * Set the file extension
     *
     * @param fileExtension
     */
    public void setFileExtension(String fileExtension);

    /**
     * Get the file name
     *
     * @return the file name
     */
    public String getFileName();

    /**
     * Set the file name
     *
     * @param fileName
     */
    public void setFileName(String fileName);

}
