package org.dd4t.test.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@ViewModel (rootElementNames = {"EmbeddableTest"})
public class EmbeddedOne extends TridionViewModelBase implements GenericModelInterface {
    @ViewModelProperty
    private String testfieldOne;
    @ViewModelProperty
    private GenericModelInterface embeddableTwo;

    public String getTestfieldOne () {
        return testfieldOne;
    }

    public void setTestfieldOne (final String testfieldOne) {
        this.testfieldOne = testfieldOne;
    }

    public GenericModelInterface getEmbeddableTwo () {
        return embeddableTwo;
    }

    public void setEmbeddableTwo (final GenericModelInterface embeddableTwo) {
        this.embeddableTwo = embeddableTwo;
    }
}
