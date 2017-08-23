package org.dd4t.test.models;

import org.dd4t.contentmodel.Keyword;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;
import org.joda.time.DateTime;

import java.util.List;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */

@ViewModel (
        viewModelNames = {"generic-content"},
        rootElementNames = {"Generic"},
        setComponentObject = true)
public class Generic extends TridionViewModelBase {

    @ViewModelProperty
    private String heading;

    @ViewModelProperty
    List<String> body;

    @ViewModelProperty
    private double numeric;

    @ViewModelProperty
    private DateTime date;

    @ViewModelProperty (entityFieldName = "externallink")
    private String externalLink;

    /**
     * Note: For multimedia you can also use the Component class
     * to set Multimedia components.
     */
    @ViewModelProperty (entityFieldName = "multimedialink")
    private Image multimedia;

    @ViewModelProperty (entityFieldName = "componentlink")
    private AbstractModelClass componentLink;

    @ViewModelProperty
    List<EmbeddedOne> embedded;

    @ViewModelProperty
    private Keyword keyword;

    public Generic() {

    }

    public String getHeading () {
        return heading;
    }

    public void setHeading (final String heading) {
        this.heading = heading;
    }

    public List<String> getBody () {
        return body;
    }

    public void setBody (final List<String> body) {
        this.body = body;
    }

    public double getNumeric () {
        return numeric;
    }

    public void setNumeric (final double numeric) {
        this.numeric = numeric;
    }

    public DateTime getDate () {
        return date;
    }

    public void setDate (final DateTime date) {
        this.date = date;
    }

    public String getExternalLink () {
        return externalLink;
    }

    public void setExternalLink (final String externalLink) {
        this.externalLink = externalLink;
    }

    public Image getMultimedia () {
        return multimedia;
    }

    public void setMultimedia (final Image multimedia) {
        this.multimedia = multimedia;
    }

    public AbstractModelClass getComponentLink () {
        return componentLink;
    }

    public void setComponentLink (final AbstractModelClass componentLink) {
        this.componentLink = componentLink;
    }


    public List<EmbeddedOne> getEmbedded () {
        return embedded;
    }

    public void setEmbedded (final List<EmbeddedOne> embedded) {
        this.embedded = embedded;
    }

    public Keyword getKeyword () {
        return keyword;
    }

    public void setKeyword (final Keyword keyword) {
        this.keyword = keyword;
    }

}
