package org.dd4t.providers;

import org.joda.time.DateTime;

import java.util.Date;

/**
 * Wraps provider results into a generic object. This is used
 * to be able to store meta information about a Tridion item next to
 * the content. In the Java version of DD4T, this is useful, as we're fetching ItemMeta
 * when fetching actual content for Pages and Binaries. This meta contains information
 * like the Last Publish Date, which by default is not stored inside the Item's Json
 * content.
 * <p/>
 * This interface can be extended in the future if so required.
 *
 * @author R. Kempees
 */
public interface ProviderResultItem<T> {

    T getSourceContent ();

    void setContentSource (T source);

    DateTime getLastPublishDate ();

    void setLastPublishDate (Date lastPublishDate);

    DateTime getRevisionDate ();

    void setRevisionDate (Date revisionDate);
}
