package org.dd4t.test.web.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@ViewModel (rootElementNames = {"embeddableTestTwo"})
public class EmbeddedTwo extends TridionViewModelBase {

    @ViewModelProperty
    private String testfieldTwo;

    public String getTestfieldTwo () {
        return testfieldTwo;
    }

    public void setTestfieldTwo (final String testfieldTwo) {
        this.testfieldTwo = testfieldTwo;
    }
}
