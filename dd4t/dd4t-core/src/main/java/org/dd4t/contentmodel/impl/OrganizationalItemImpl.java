package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.dd4t.contentmodel.OrganizationalItem;

public class OrganizationalItemImpl extends BaseItem implements OrganizationalItem {

    @JsonProperty("PublicationId")
    private String publicationId;

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }
}
