package org.dd4t.databind.viewmodel.test;

import org.dd4t.contentmodel.Component;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

import java.util.List;

@ViewModel(viewModelNames = {"storyshort_2column", "storyhighlight_4column", "story_4column"}, setComponentObject = true)
public class Story extends TridionViewModelBase {

	@ViewModelProperty(entityFieldName = "heading")
	private String heading;

	@ViewModelProperty(entityFieldName = "multimedia")
	private List<Component> imageWrapper;

	public String getHeading() {
		return heading;
	}

	public void setHeading(String heading) {
		this.heading = heading;
	}

	public List<Component> getImageWrapper() {
		return imageWrapper;
	}

	public void setImageWrapper(List<Component> imageWrapper) {
		this.imageWrapper = imageWrapper;
	}
}