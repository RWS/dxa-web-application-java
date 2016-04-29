package org.dd4t.test.web.models;

import org.dd4t.contentmodel.Keyword;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.ViewModelBase;
import org.joda.time.DateTime;

import java.util.List;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@ViewModel (rootElementNames = "Event", setComponentObject = true)
public class Event extends ViewModelBase {
    @ViewModelProperty (entityFieldName = "Title")
    private String title;


    @ViewModelProperty(entityFieldName = "EventStart",isMetadata = true)
    private DateTime eventStart;

    @ViewModelProperty(entityFieldName = "Color",isMetadata = true)
    private List<Keyword> color;

    public String getTitle () {
        return title;
    }

    public void setTitle (final String title) {
        this.title = title;
    }

    public DateTime getEventStart () {
        return eventStart;
    }

    public void setEventStart (final DateTime eventStart) {
        this.eventStart = eventStart;
    }

    public List<Keyword> getColor () {
        return color;
    }

    public void setColor (final List<Keyword> color) {
        this.color = color;
    }
}
