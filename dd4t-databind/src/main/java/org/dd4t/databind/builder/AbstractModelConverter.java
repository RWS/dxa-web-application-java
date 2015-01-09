package org.dd4t.databind.builder;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.databind.viewmodel.base.ModelFieldMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public abstract class AbstractModelConverter {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractModelConverter.class);

	protected static String getFieldKeyForModelProperty (final String fieldName, final ModelFieldMapping m) {
		final String fieldKey;
		if (m.getViewModelProperty() == null) {
			fieldKey = fieldName;
		} else {
			if (StringUtils.isEmpty(m.getViewModelProperty().entityFieldName())) {
				fieldKey = fieldName;
			} else {
				fieldKey = m.getViewModelProperty().entityFieldName();
			}
		}
		return fieldKey;
	}

	protected static <T extends BaseViewModel> void addToListTypeField (final T model, final Field modelField, final BaseViewModel strongModel) throws IllegalAccessException {
		List list = (List) modelField.get(model);
		if (list == null) {
			list = new ArrayList();
			list.add(strongModel);
			modelField.set(model, list);
		} else {
			list.add(strongModel);
		}
	}

	protected <T extends BaseViewModel> void setFieldValue (final T model, final Field f, final org.dd4t.contentmodel.Field renderedField) throws IllegalAccessException {

		boolean isMultiValued = false;
		if (f.getType().equals(List.class)) {
			isMultiValued = true;
		}

		List<Object> values = renderedField.getValues();

		if (values != null && !values.isEmpty()) {

			if (isMultiValued) {
				LOG.debug("Setting multivalued field: {}", f.getName());
				f.set(model, renderedField.getValues());
			} else {
				f.set(model, renderedField.getValues().get(0));
			}

			if (model instanceof TridionViewModel) {
				setXpm((TridionViewModel) model, renderedField, isMultiValued);
			}

		} else {
			LOG.debug("No value(s) found!");
			if (model instanceof TridionViewModel) {
				setXpm((TridionViewModel) model, renderedField, isMultiValued);
			}
		}
	}

	// TODO: only do when XPM enabled
	protected static void setXpm (final TridionViewModel model, final org.dd4t.contentmodel.Field renderedField, final boolean isMultiValued) {
		String xPath = renderedField.getXPath();
		model.addXpmEntry(renderedField.getName(), xPath, isMultiValued);
	}
}
