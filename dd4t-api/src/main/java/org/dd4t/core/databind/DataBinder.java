package org.dd4t.core.databind;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.exceptions.SerializationException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

/**
 * test
 *
 * @author R. Kempees
 * @since 01/12/14.
 */
public interface DataBinder {

	public HashMap<String,BaseViewModel> buildModels(final Object source, final Set<String> modelNames, final String templateUri) throws SerializationException;
	public <T extends BaseViewModel> T buildModel(final Object rawData, final String modelName, final String templateUri) throws SerializationException;
	public <T extends BaseViewModel> T buildModel (final Object source, final Class modelClass, final String templateUri) throws SerializationException;
	public <T extends Page> T buildPage(final String source,final Class<T> aClass) throws SerializationException;
	public ComponentPresentation buildDynamicComponentPresentation (final ComponentPresentation componentPresentation, final Class<? extends Component> aClass) throws SerializationException;
	public <T extends Component> T buildComponent(final Object source, final Class<T> aClass) throws SerializationException;

	public String findComponentTemplateViewName(ComponentTemplate template) throws IOException;
	/*
	 * Object should be cast to whatever the implementation has as raw
	 * deserialization object. For Jackson this is JsonNode
	 */
	public String getRootElementName(Object componentNode);
	public boolean renderDefaultComponentModelsOnly ();
	public boolean renderDefaultComponentsIfNoModelFound();
}
