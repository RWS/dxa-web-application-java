package org.dd4t.test.models;


import org.dd4t.contentmodel.Multimedia;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;
import org.joda.time.DateTime;

import java.util.List;

@ViewModel (viewModelNames = {"newsdetail"}, rootElementNames = {"newsItem"}, setComponentObject = true)

public class NewsItem extends TridionViewModelBase {

    @ViewModelProperty
    private String heading;

    @ViewModelProperty
    private String introduction;

    @ViewModelProperty
    List<EmbeddedParagraph> body;

    @ViewModelProperty (entityFieldName = "image")
    private Multimedia image;

    @ViewModelProperty (isMetadata = true)
    private String author;

    @ViewModelProperty (isMetadata = true)
    private DateTime publishDate;

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public List<EmbeddedParagraph> getBody() {
        return body;
    }

    public void setBody(List<EmbeddedParagraph> body) {
        this.body = body;
    }

    public Multimedia getImage() {
        return image;
    }

    public void setImage(Multimedia image) {
        this.image = image;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public DateTime getPublishDate() {
        return publishDate;
    }

    public void setPublishDate(DateTime publishDate) {
        this.publishDate = publishDate;
    }


}
