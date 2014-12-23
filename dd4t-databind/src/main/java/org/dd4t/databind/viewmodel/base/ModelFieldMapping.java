package org.dd4t.databind.viewmodel.base;

import org.dd4t.databind.annotations.ViewModelProperty;

import java.lang.reflect.Field;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public class ModelFieldMapping {
	private ViewModelProperty viewModelProperty;
	private Field field;
	public ModelFieldMapping(ViewModelProperty modelProperty, Field f) {
		this.field = f;
		this.viewModelProperty = modelProperty;
	}

	public ViewModelProperty getViewModelProperty () {
		return viewModelProperty;
	}

	public Field getField () {
		return field;
	}
}
