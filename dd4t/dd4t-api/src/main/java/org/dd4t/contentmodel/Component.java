package org.dd4t.contentmodel;

import org.joda.time.DateTime;

/**
 * Interface for all components
 *
 * @author bjornl
 */
public interface Component extends RepositoryLocalItem {

    /**
     * Get the schema of the component
     *
     * @return the schema
     */
    public Schema getSchema();

    /**
     * Set the schema of the component
     *
     * @param schema
     */
    public void setSchema(Schema schema);

    /**
     * Get the resolved url
     *
     * @return the resolved url
     */
    public String getResolvedUrl();

    /**
     * Set the resolved url
     *
     * @param resolvedUrl
     */
    public void setResolvedUrl(String resolvedUrl);


    /**
     * Get the last published date
     *
     * @return the last published date
     */
    public DateTime getLastPublishedDate();

    /**
     * Set the last published date
     *
     * @param date published date
     */
    public void setLastPublishedDate(DateTime date);
}
