#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ${package}.models;

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
@ViewModel(viewModelNames = {"footnote","product-details"}, setComponentObject = true)
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
