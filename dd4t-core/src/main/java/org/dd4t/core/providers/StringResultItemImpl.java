package org.dd4t.core.providers;

import org.dd4t.core.util.Constants;
import org.dd4t.providers.ProviderResultItem;
import org.joda.time.DateTime;

import java.util.Date;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
public class StringResultItemImpl implements ProviderResultItem<String> {

    private String sourceContent;
    private DateTime lastPublishDate;
    private DateTime revisionDate;


    @Override
    public String getSourceContent () {
        return sourceContent;
    }

    @Override
    public void setContentSource (final String source) {
        this.sourceContent = source;
    }

    @Override
    public DateTime getLastPublishDate () {
        return this.lastPublishDate != null ? this.lastPublishDate : Constants.THE_YEAR_ZERO;
    }

    @Override
    public void setLastPublishDate (final Date lastPublishDate) {

        if (lastPublishDate == null) {
            this.lastPublishDate = Constants.THE_YEAR_ZERO;
        } else {
            this.lastPublishDate = new DateTime(lastPublishDate);
        }
    }

    @Override
    public DateTime getRevisionDate () {
        return this.revisionDate;
    }

    @Override
    public void setRevisionDate (final Date revisionDate) {
        if (revisionDate == null) {
            this.revisionDate = Constants.THE_YEAR_ZERO;
        } else {
            this.revisionDate = new DateTime(revisionDate);
        }
    }
}
