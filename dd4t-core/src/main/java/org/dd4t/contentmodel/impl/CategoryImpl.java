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
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Category;
import org.dd4t.contentmodel.Keyword;

import java.util.LinkedList;
import java.util.List;

public class CategoryImpl extends BaseItem implements Category {

    @JsonProperty ("Keywords")
    @JsonDeserialize (contentAs = KeywordImpl.class)
    private List<Keyword> keywords;

    @Override
    public List<Keyword> getKeywords () {
        List<Keyword> l = new LinkedList<>();

        if (keywords != null) {
            for (Keyword k : keywords) {
                l.add(k);
            }
        }

        return l;
    }

    @Override
    public void setKeywords (List<Keyword> keywords) {
        List<Keyword> l = new LinkedList<>();

        for (Keyword k : keywords) {
            l.add(k);
        }

        this.keywords = l;
    }
}
