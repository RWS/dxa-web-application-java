package org.dd4t.contentmodel;

/**
 * Interface for all items except for publications
 *
 * @author bjornl
 */
public interface RepositoryLocalItem extends Item {
    /**
     * Get the organizational item
     *
     * @return
     */
    public OrganizationalItem getOrganizationalItem();

    /**
     * Set the organizational item
     *
     * @param organizationalItem
     */
    public void setOrganizationalItem(OrganizationalItem organizationalItem);

    /**
     * Get the publication
     *
     * @return
     */
    public Publication getOwningPublication();

    /**
     * Set the owning publication
     *
     * @param publication
     */
    public void setOwningPublication(Publication publication);

    /**
     * Get the owning publication
     *
     * @return
     */
    public Publication getPublication();

    /**
     * Set the publication
     *
     * @param publication
     */
    public void setPublication(Publication publication);
}
