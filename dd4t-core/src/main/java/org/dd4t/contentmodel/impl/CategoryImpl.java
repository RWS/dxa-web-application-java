package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.Keyword;

import java.util.LinkedList;
import java.util.List;

public class CategoryImpl extends BaseItem implements Category {

    @JsonProperty("Keywords")
    @JsonDeserialize(contentAs = KeywordImpl.class)
    private List<Keyword> keywords;

    @Override
    public List<Keyword> getKeywords() {
        List<Keyword> l = new LinkedList<>();

        if (keywords != null) {
            for (Keyword k : keywords) {
                l.add(k);
            }
        }

        return l;
    }

    @Override
    public void setKeywords(List<Keyword> keywords) {
        List<Keyword> l = new LinkedList<>();

        for (Keyword k : keywords) {
            l.add(k);
        }

        this.keywords = l;
    }
}
