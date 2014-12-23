package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.OrganizationalItem;
import org.dd4t.contentmodel.Publication;
import org.dd4t.contentmodel.RepositoryLocalItem;

/**
 * Base class for all tridion items except for publications and organizational items
 *
 * @author bjornl
 */
public abstract class BaseRepositoryLocalItem extends BaseItem implements RepositoryLocalItem {

    @JsonProperty("Publication")
    @JsonDeserialize(as = PublicationImpl.class)
    private Publication publication;

    @JsonProperty("OwningPublication")
    @JsonDeserialize(as = PublicationImpl.class)
    private Publication owningPublication;

    @JsonProperty("Folder")
    @JsonDeserialize(as = OrganizationalItemImpl.class)
    private OrganizationalItem organizationalItem;

    /**
     * Get the organizational item
     */
    @Override
    public OrganizationalItem getOrganizationalItem() {
        return organizationalItem;
    }

    /**
     * Set the organizational item
     */
    public void setOrganizationalItem(OrganizationalItem organizationalItem) {
        this.organizationalItem = organizationalItem;
    }

    /**
     * Get the publication
     */
    @Override
    public Publication getOwningPublication() {
        return owningPublication;
    }

    /**
     * Set the publication
     *
     * @param publication
     */
    public void setOwningPublication(Publication publication) {
        this.owningPublication = publication;
    }

    /**
     * Get the publication
     */
    @Override
    public Publication getPublication() {
        return publication;
    }

    /**
     * Set the publication
     *
     * @param publication
     */
    @Override
    public void setPublication(Publication publication) {
        this.publication = publication;
    }
}