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
import org.dd4t.contentmodel.*;
import org.dd4t.core.util.DateUtils;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Base class for all tridion items except for publications and organizational items
 *
 * @author bjornl
 */
public abstract class BaseRepositoryLocalItem extends BaseItem implements RepositoryLocalItem {

    @JsonProperty ("RevisionDate")
    protected String revisionDateAsString;

    @JsonProperty ("Publication")
    @JsonDeserialize (as = PublicationImpl.class)
    protected Publication publication;

    @JsonProperty ("OwningPublication")
    @JsonDeserialize (as = PublicationImpl.class)
    protected Publication owningPublication;

    // TODO: move lower in the chain
    @JsonProperty ("Folder")
    @JsonDeserialize (as = OrganizationalItemImpl.class)
    protected OrganizationalItem organizationalItem;

    @JsonProperty ("LastPublishedDate")
    protected String lastPublishedDateAsString;

    @JsonProperty ("Version")
    protected int version;

    @JsonProperty ("MetadataFields")
    @JsonDeserialize (contentAs = BaseField.class)
    protected Map<String, Field> metadata;

    @JsonProperty ("Categories")
    @JsonDeserialize (contentAs = CategoryImpl.class)
    protected List<Category> categories;
    @JsonProperty ("Schema")
    @JsonDeserialize (as = SchemaImpl.class)
    private Schema schema;


    /**
     * Get the organizational item
     */
    @Override
    public OrganizationalItem getOrganizationalItem () {
        return organizationalItem;
    }

    /**
     * Set the organizational item
     */
    @Override
    public void setOrganizationalItem (OrganizationalItem organizationalItem) {
        this.organizationalItem = organizationalItem;
    }

    /**
     * Get the publication
     */
    @Override
    public Publication getOwningPublication () {
        return owningPublication;
    }

    /**
     * Set the publication
     *
     * @param publication
     */
    @Override
    public void setOwningPublication (Publication publication) {
        this.owningPublication = publication;
    }

    /**
     * Get the publication
     */
    @Override
    public Publication getPublication () {
        return publication;
    }

    /**
     * Set the publication
     *
     * @param publication
     */
    @Override
    public void setPublication (Publication publication) {
        this.publication = publication;
    }


    public DateTime getRevisionDate () {
        if (revisionDateAsString == null || revisionDateAsString.isEmpty()) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(revisionDateAsString);
    }

    public void setRevisionDate (DateTime date) {
        this.revisionDateAsString = date.toString();
    }

    @Override
    public DateTime getLastPublishedDate () {
        if (lastPublishedDateAsString == null || lastPublishedDateAsString.isEmpty()) {
            return new DateTime();
        }
        return DateUtils.convertStringToDate(lastPublishedDateAsString);
    }

    @Override
    public void setLastPublishedDate (DateTime date) {
        this.lastPublishedDateAsString = DateUtils.convertDateToString(date);
    }

    public int getVersion () {

        return version;
    }

    public void setVersion (int version) {

        this.version = version;
    }

    public Map<String, Field> getMetadata () {
        if (metadata == null) {
            metadata = new HashMap<>();
        }
        return metadata;
    }

    public void setMetadata (Map<String, Field> metadata) {
        this.metadata = metadata;
    }

    public List<Category> getCategories () {
        return categories;
    }

    public void setCategories (List<Category> categories) {
        this.categories = categories;
    }

    @Override
    public Schema getSchema () {
        return schema;
    }

    public void setSchema (Schema schema) {
        this.schema = schema;
    }
}