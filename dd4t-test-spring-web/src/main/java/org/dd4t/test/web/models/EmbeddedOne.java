package org.dd4t.test.web.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@ViewModel (rootElementNames = {"EmbeddableTest"})
public class EmbeddedOne extends TridionViewModelBase {
    @ViewModelProperty
    private String testfieldOne;
    @ViewModelProperty
    private EmbeddedTwo embeddableTwo;

    public String getTestfieldOne () {
        return testfieldOne;
    }

    public void setTestfieldOne (final String testfieldOne) {
        this.testfieldOne = testfieldOne;
    }

    public EmbeddedTwo getEmbeddableTwo () {
        return embeddableTwo;
    }

    public void setEmbeddableTwo (final EmbeddedTwo embeddableTwo) {
        this.embeddableTwo = embeddableTwo;
    }
}
