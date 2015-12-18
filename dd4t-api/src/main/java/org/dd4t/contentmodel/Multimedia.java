/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
    BinaryData getBinaryData();

    /**
     * Set the binary data
     *
     * @param binaryData
     */
    void setBinaryData(BinaryData binaryData);

    /**
     * Get the height of the binary
     *
     * @return the height
     */
    int getHeight();

    /**
     * Set the height of the binary
     *
     * @param height
     */
    void setHeight(int height);

    /**
     * Get the width of the binary
     *
     * @return
     */
    int getWidth();

    /**
     * Set the width of the binary
     *
     * @param width
     */
    void setWidth(int width);

    /**
     * Get the size of the binary
     *
     * @return Size in bytes
     */
    int getSize();

    /**
     * Set the size of the binary
     *
     * @param size in bytes
     */
    void setSize(int size);

    /**
     * Get the alt text of the binary
     *
     * @return the alt text
     */
    String getAlt();

    /**
     * Set the al text
     *
     * @param alt
     */
    void setAlt(String alt);

    /**
     * Get the URL of the binary
     *
     * @return the URL
     */
    String getUrl();

    /**
     * Set the URL
     *
     * @param url
     */
    void setUrl(String url);

    /**
     * Get the mime type of the binary
     *
     * @return the mime type
     */
    String getMimeType();

    /**
     * Set the mime type
     *
     * @param mimeType type
     */
    void setMimeType(String mimeType);

    /**
     * Get the file extension
     *
     * @return the file extension
     */
    String getFileExtension();

    /**
     * Set the file extension
     *
     * @param fileExtension
     */
    void setFileExtension(String fileExtension);

    /**
     * Get the file name
     *
     * @return the file name
     */
    String getFileName();

    /**
     * Set the file name
     *
     * @param fileName
     */
    void setFileName(String fileName);

}
