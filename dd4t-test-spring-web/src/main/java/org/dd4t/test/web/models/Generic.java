package org.dd4t.test.web.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

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
	List<EmbeddedOne> embedded;

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

	public List<EmbeddedOne> getEmbedded () {
		return embedded;
	}

	public void setEmbedded (final List<EmbeddedOne> embedded) {
		this.embedded = embedded;
	}
}
