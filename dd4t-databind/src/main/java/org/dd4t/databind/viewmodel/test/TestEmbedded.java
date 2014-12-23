package org.dd4t.databind.viewmodel.test;

import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
@ViewModel(rootElementNames = "appCard")
public class TestEmbedded extends TridionViewModelBase {
	@ViewModelProperty(entityFieldName = "heading")
	public String heading;
}
