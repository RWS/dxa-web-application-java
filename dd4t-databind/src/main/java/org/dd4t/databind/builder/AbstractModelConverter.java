package org.dd4t.databind.builder;

import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.databind.util.TypeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public abstract class AbstractModelConverter {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractModelConverter.class);

	protected <T extends BaseViewModel> void setFieldValue (final T model, final Field f, final org.dd4t.contentmodel.Field renderedField) throws IllegalAccessException {

		boolean isMultiValued = false;
		Type typeOfFieldToSet;
		if (f.getType().equals(List.class)) {
			isMultiValued = true;
			typeOfFieldToSet = TypeUtils.getRuntimeTypeOfTypeParameter(f.getGenericType());
		} else {
			typeOfFieldToSet = f.getType();
		}


		List<Object> values = renderedField.getValues();

		if (values != null && !values.isEmpty()) {

			if (renderedField.getFieldType().equals(FieldType.EMBEDDED) && !TypeUtils.classIsViewModel((Class<?>) typeOfFieldToSet)) {
// TODO: this needs to be set on the Embedded class on the parent field if it is list!
				if (isMultiValued) {
					for (Object o : values) {
						final FieldSet fieldSet = (FieldSet) o;
						final Map<String, org.dd4t.contentmodel.Field> content = fieldSet.getContent();
						f.set(model, content.containsKey(f.getName()) ? content.get(f.getName()).getValues().get(0) : null);
					}
				} else {
					LOG.warn("Probably you will want to have a List wrapping this EmbeddedField");
					final FieldSet fieldSet = (FieldSet) values.get(0);
					final Map<String, org.dd4t.contentmodel.Field> content = fieldSet.getContent();
					f.set(model, content.containsKey(f.getName()) ? content.get(f.getName()).getValues().get(0) : null);
				}
			} else {
				if (isMultiValued) {
					LOG.debug("Setting multivalued field: {}", f.getName());
					f.set(model, renderedField.getValues());
				} else {
					f.set(model, renderedField.getValues().get(0));
				}

				if (model instanceof TridionViewModel) {
					setXpm((TridionViewModel) model, renderedField, isMultiValued);
				}
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
