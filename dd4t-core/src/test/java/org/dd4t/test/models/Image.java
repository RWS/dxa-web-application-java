package org.dd4t.test.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@ViewModel
public class Image extends TridionViewModelBase {

    @ViewModelProperty(isMetadata = true)
    private String focusPoint;

    @ViewModelProperty(entityFieldName = "Url")
    private String url;

    @ViewModelProperty(entityFieldName = "Height")
    private int height;

    @ViewModelProperty(entityFieldName = "Width")
    private int width;

    @ViewModelProperty(entityFieldName = "Size")
    private Integer size;

    @ViewModelProperty(entityFieldName = "Alt")
    private String alt;

    @ViewModelProperty(entityFieldName = "MimeType")
    private String mimeType;

    @ViewModelProperty(entityFieldName = "FileExtension")
    private String fileExtension;

    @ViewModelProperty(entityFieldName = "FileName")
    private String fileName;

    public String getFocusPoint () {
        return focusPoint;
    }

    public void setFocusPoint (final String focusPoint) {
        this.focusPoint = focusPoint;
    }

    public String getUrl () {
        return url;
    }

    public void setUrl (final String url) {
        this.url = url;
    }

    public int getHeight () {
        return height;
    }

    public void setHeight (final int height) {
        this.height = height;
    }

    public int getWidth () {
        return width;
    }

    public void setWidth (final int width) {
        this.width = width;
    }

    public Integer getSize () {
        return size;
    }

    public void setSize (final Integer size) {
        this.size = size;
    }

    public String getAlt () {
        return alt;
    }

    public void setAlt (final String alt) {
        this.alt = alt;
    }

    public String getMimeType () {
        return mimeType;
    }

    public void setMimeType (final String mimeType) {
        this.mimeType = mimeType;
    }

    public String getFileExtension () {
        return fileExtension;
    }

    public void setFileExtension (final String fileExtension) {
        this.fileExtension = fileExtension;
    }

    public String getFileName () {
        return fileName;
    }

    public void setFileName (final String fileName) {
        this.fileName = fileName;
    }
}
