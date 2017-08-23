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
import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.Keyword;
import org.simpleframework.xml.ElementList;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CategoryImpl extends BaseItem implements Category, Serializable {

    private static final long serialVersionUID = 4141821829912175584L;

	@ElementList(name = "keywords", required = false, type = KeywordImpl.class)
    @JsonProperty ("Keywords")
    @JsonDeserialize (contentAs = KeywordImpl.class)
    private List<Keyword> keywords;

    @Override
    public List<Keyword> getKeywords () {
        if (this.keywords == null) {
            return new LinkedList<>();
        }
        return this.keywords;
    }

    @JsonSetter ("Keywords")
    @Override
    public void setKeywords (List<Keyword> keywordValues) {
        this.keywords = keywordValues;
    }

    // DD4T 2.0.2 template support
    @JsonSetter ("KeywordValues")
    private void setKeywordValues (List<Keyword> keywordValues) {
        this.keywords = keywordValues;
    }
}
