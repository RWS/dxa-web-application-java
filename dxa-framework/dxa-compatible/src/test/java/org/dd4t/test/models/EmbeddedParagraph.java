package org.dd4t.test.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

@ViewModel (rootElementNames = {"paragraph"})
public class EmbeddedParagraph extends TridionViewModelBase {

    @ViewModelProperty (entityFieldName = "subtitle")
    private String subTitle;

    @ViewModelProperty
    private String paragraph;

    public String getSubTitle() {
        return subTitle;
    }

    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getParagraph() {
        return this.paragraph;
    }

    public void setParagraph(String paragraph) {
        this.paragraph = paragraph;
    }


}
