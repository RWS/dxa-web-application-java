package org.dd4t.contentmodel;

import org.joda.time.DateTime;

public interface Page extends RepositoryLocalItem {
    /**
     * Get the metadata schema
     *
     * @return the metadata schema
     */
    public Schema getSchema();

    /**
     * Set the metadata schema
     *
     * @param schema
     */
    public void setSchema(Schema schema);

    /**
     * Get the last published date
     *
     * @return the last published date
     */
    public DateTime getLastPublishedDate();

    /**
     * Set the last published date
     *
     * @param date
     */
    public void setLastPublishedDate(DateTime date);
}
