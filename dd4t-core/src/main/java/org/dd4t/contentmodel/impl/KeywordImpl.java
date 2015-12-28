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

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.Keyword;
import org.dd4t.core.util.TCMURI;

import java.util.List;
import java.util.Map;

@JsonIdentityInfo (generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@id")
public class KeywordImpl extends BaseItem implements Keyword {

    @JsonProperty ("ChildKeywords")
    @JsonDeserialize (contentAs = KeywordImpl.class)
    private List<Keyword> childKeywords;

    @JsonProperty ("ClassifiedItems")
    private List<TCMURI> classifiedItems;

    @JsonProperty ("Description")
    private String description;

    @JsonProperty ("Key")
    private String key;

    @JsonProperty ("MetadataFields")
    @JsonDeserialize (contentAs = BaseField.class)
    private Map<String, Field> metadata;

    @JsonProperty ("ParentKeywords")
    @JsonDeserialize (contentAs = KeywordImpl.class)
    private List<Keyword> parentKeywords;

    @JsonProperty ("Path")
    private String path;

    @JsonProperty ("RelatedKeywords")
    private List<TCMURI> relatedKeywords;

    @JsonProperty ("TaxonomyId")
    private String taxonomyId;

    @Override
    public String getDescription () {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    @Override
    public String getKey () {
        return key;
    }

    public void setKey (String key) {
        this.key = key;
    }

    @Override
    public List<Keyword> getChildKeywords () {
        return childKeywords;
    }

    public void setChildKeywords (List<Keyword> childKeywords) {
        this.childKeywords = childKeywords;
    }

    @Override
    public boolean hasChildren () {
        return childKeywords != null && !childKeywords.isEmpty();
    }

    @Override
    public List<Keyword> getParentKeywords () {
        return parentKeywords;
    }

    public void setParentKeywords (List<Keyword> parentKeywords) {
        this.parentKeywords = parentKeywords;
    }

    @Override
    public boolean hasParents () {
        return parentKeywords != null && !parentKeywords.isEmpty();
    }

    @Override
    public List<TCMURI> getRelatedKeywords () {
        return relatedKeywords;
    }

    public void setRelatedKeywords (List<TCMURI> relatedKeywords) {
        this.relatedKeywords = relatedKeywords;
    }

    @Override
    public boolean hasRelatedKeywords () {
        return relatedKeywords != null && !relatedKeywords.isEmpty();
    }

    @Override
    public String getPath () {
        return path;
    }

    @Override
    public void setPath (String path) {
        this.path = path;
    }

    @Override
    public List<TCMURI> getClassifiedItems () {
        return classifiedItems;
    }

    public void setClassifiedItems (List<TCMURI> classifiedItems) {
        this.classifiedItems = classifiedItems;
    }

    @Override
    public boolean hasClassifiedItems () {
        return classifiedItems != null && !classifiedItems.isEmpty();
    }

    @Override
    public String getTaxonomyId () {
        return taxonomyId;
    }

    @Override
    public void setTaxonomyId (String taxonomyId) {
        this.taxonomyId = taxonomyId;
    }

    @Override
    public Map<String, Field> getMetadata () {
        return metadata;
    }

    @Override
    public void setMetadata (Map<String, Field> metadata) {
        this.metadata = metadata;
    }
}
