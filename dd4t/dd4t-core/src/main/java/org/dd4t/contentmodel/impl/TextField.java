package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;

import java.util.LinkedList;
import java.util.List;

public class TextField extends BaseField implements Field {

    @JsonProperty("CategoryId")
    private String categoryId;

    @JsonProperty("CategoryName")
    private String categoryName;

    public TextField() {
        setFieldType(FieldType.Text);
    }

    @Override
    public List<Object> getValues() {
        List<String> textValues = getTextValues();
        List<Object> l = new LinkedList<>();

        for (String s : textValues) {
            l.add(s);
        }

        return l;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }
}
