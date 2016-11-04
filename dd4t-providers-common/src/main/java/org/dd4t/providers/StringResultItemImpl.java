package org.dd4t.providers;

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
    private int itemId;
    private int publicationId;    
    
    public StringResultItemImpl(int pubid, int itemid){
    	this.itemId = itemid;
    	this.publicationId = pubid;
    }


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

    @Override
	public int getPublicationId() {
		return publicationId;
	}

	public void setPublicationId(int publicationId) {
		this.publicationId = publicationId;
	}

    @Override
	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}
}
