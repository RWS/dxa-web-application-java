package org.dd4t.databind.viewmodel.test;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

import java.util.List;

/**
 *
 * If you don't know the viewName on a template, or if you just want to reuse
 * this model across multiple views, using the same component, the just set the
 * rootElementNames parameter
 *
 * @author R. Kempees
 */

@ViewModel(viewModelNames = {"benefit","appsbanner"},rootElementNames = "benefits",setComponentObject = true)
public class TestViewModel extends TridionViewModelBase {

	@ViewModelProperty(entityFieldName = "heading")
	public String heading;

	@ViewModelProperty
	public String body;

	@ViewModelProperty
	public List<TestVideoModel> video;

	@ViewModelProperty
	public List<TestEmbedded> appCard;

	public String getHeading () {
		return heading;
	}

	public void setHeading (final String heading) {
		this.heading = heading;
	}
}
