package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Keyword;

import java.util.LinkedList;
import java.util.List;

public class KeywordField extends BaseField implements Field {

    @JsonProperty("CategoryName")
    private String categoryName;

    @JsonProperty("CategoryId")
    private String categoryId;

    public KeywordField() {
        setFieldType(FieldType.Keyword);
    }

    @Override
    public List<Object> getValues() {
        List<Keyword> keywordValues = getKeywordValues();
        List<Object> l = new LinkedList<>();

        for (Keyword k : keywordValues) {
            l.add(k);
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