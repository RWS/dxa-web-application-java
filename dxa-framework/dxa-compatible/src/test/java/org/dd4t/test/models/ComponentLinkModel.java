package org.dd4t.test.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */
@ViewModel(viewModelNames = {"generic-content"}, rootElementNames = {""}, setComponentObject = true)
public class ComponentLinkModel extends AbstractModelClass {

    @ViewModelProperty
    private String heading;

    public String getHeading() {
        return heading;
    }

    public void setHeading(final String heading) {
        this.heading = heading;
    }
}