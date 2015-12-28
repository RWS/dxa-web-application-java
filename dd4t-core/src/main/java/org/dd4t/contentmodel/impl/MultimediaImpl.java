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

package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.BinaryData;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Multimedia;

import java.util.LinkedList;
import java.util.List;

public class MultimediaImpl extends BaseField implements Multimedia {

    private BinaryData binaryData;

    @JsonProperty ("Height")
    private int height;

    @JsonProperty ("Width")
    private int width;

    @JsonProperty ("Size")
    private int size;

    @JsonProperty ("Alt")
    private String alt;

    @JsonProperty ("Url")
    private String url;

    @JsonProperty ("MimeType")
    private String mimeType;

    @JsonProperty ("FileExtension")
    private String fileExtension;

    @JsonProperty ("FileName")
    private String fileName;

    @Override
    public BinaryData getBinaryData () {
        return this.binaryData;
    }

    @Override
    public void setBinaryData (BinaryData binaryData) {
        this.binaryData = binaryData;
    }

    @Override
    public int getHeight () {
        return height;
    }

    @Override
    public void setHeight (int height) {
        this.height = height;
    }

    @Override
    public int getWidth () {
        return width;
    }

    @Override
    public void setWidth (int width) {
        this.width = width;
    }

    @Override
    public int getSize () {
        return size;
    }

    @Override
    public void setSize (int size) {
        this.size = size;
    }

    @Override
    public String getAlt () {
        return alt;
    }

    @Override
    public void setAlt (String alt) {
        this.alt = alt;
    }

    @Override
    public String getUrl () {
        return url;
    }

    @Override
    public void setUrl (String url) {
        this.url = url;
    }

    @Override
    public String getMimeType () {
        return mimeType;
    }

    @Override
    public void setMimeType (String mimeType) {
        this.mimeType = mimeType;
    }

    @Override
    public String getFileExtension () {
        return fileExtension;
    }

    @Override
    public void setFileExtension (String fileExtension) {
        this.fileExtension = fileExtension;
    }

    @Override
    public String getFileName () {
        return fileName;
    }

    @Override
    public void setFileName (String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Object> getValues () {
        List<Component> compValues = getLinkedComponentValues();
        List<Object> l = new LinkedList<Object>();

        for (Component c : compValues) {
            l.add(c);
        }

        return l;
    }
}
