package org.dd4t.test.web.models;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

import java.util.List;

/**
 * dd4t-2
 *
 * Example. REMOVE when building
 *
 * Explanation:
 * - Always extend from TridionViewModelBase if content comes from Tridion
 * - Always set the @ViewModel annotation on the class. The
 *    viewModelNames match the names of the Component JSP views
 * - Set the @ViewModelProperty annotation in case field names differ from the Json names
 *
 * @author R. Kempees
 */
@ViewModel(viewModelNames = "footnotes")
public class GenericTextField extends TridionViewModelBase {
	@ViewModelProperty(entityFieldName = "textfield")
	private List<String> textFields;

	public List<String> getTextFields () {
		return textFields;
	}

	public void setTextFields (final List<String> textFields) {
		this.textFields = textFields;
	}
}
