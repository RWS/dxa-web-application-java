package org.dd4t.databind.viewmodel.base;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.annotations.ViewModelProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * test
 *
 * @author R. Kempees
 */
public abstract class ViewModelBase implements BaseViewModel {
	private static final Logger LOG = LoggerFactory.getLogger(ViewModelBase.class);
	private final List<String> viewModelNames;
	private HashMap<String,Object> modelProperties;
	private boolean setGenericComponentOnComponentPresentation;
	private boolean setRawDataOnModel;
	private Object rawData;

	/**
	 * Default constructor. Leave this a no arg constructor!
	 *
	 * The constructor is fired at runtime and adds the associated
	 * view model names on the List.
	 */
	public ViewModelBase(){
		this.viewModelNames = new ArrayList<>();
		this.modelProperties = new HashMap<>();
		setGenericParameters();
		setFieldParameters();
	}

	private void setGenericParameters () {
		LOG.trace("Setting generic parameters on model.");
		final ViewModel viewModelAnnotation = this.getClass().getAnnotation(ViewModel.class);
		if (null != viewModelAnnotation) {
			final String[] rootElements = viewModelAnnotation.rootElementNames();
			if (rootElements != null && rootElements.length > 0) {
				this.viewModelNames.addAll(Arrays.asList(rootElements));
			}
			final String[] viewModels = viewModelAnnotation.viewModelNames();
			if (viewModels != null && viewModels.length > 0) {
				this.viewModelNames.addAll(Arrays.asList(viewModels));
			}
			this.setGenericComponentOnComponentPresentation = viewModelAnnotation.setComponentObject();
			this.setRawDataOnModel = viewModelAnnotation.setRawData();
		}
	}

	private void setFieldParameters() {
		Field[] fields = this.getClass().getDeclaredFields();
		for (Field f : fields) {
			ViewModelProperty viewModelProperty = f.getAnnotation(ViewModelProperty.class);
			if (viewModelProperty != null) {
				String viewModelPropertyName;
				if (!StringUtils.isEmpty(viewModelProperty.entityFieldName())){
					viewModelPropertyName = viewModelProperty.entityFieldName();
				}else {
					viewModelPropertyName = f.getName();
				}
				this.modelProperties.put(viewModelPropertyName,new ModelFieldMapping(viewModelProperty,f));
			}
		}
	}

	@Override public List<String> getViewNames () {
		return viewModelNames;
	}

	@Override public boolean setRawDataOnModel () {
		return setRawDataOnModel;
	}

	@Override public void setRawData (Object data) {
		this.rawData = data;
	}

	@Override public String getRawDataAsString () {
		if (this.rawData != null) {
			return (String)rawData;
		}
		return null;
	}

	@Override public boolean setGenericComponentOnComponentPresentation () {
		return setGenericComponentOnComponentPresentation;
	}

	public HashMap<String,Object> getModelProperties() {
		return this.modelProperties;
	}
}
