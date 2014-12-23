package org.dd4t.databind.builder.json;

import com.fasterxml.jackson.databind.JsonNode;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.ModelConverter;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.databind.annotations.ViewModel;
import org.dd4t.databind.builder.AbstractModelConverter;
import org.dd4t.databind.util.Constants;
import org.dd4t.databind.util.JsonUtils;
import org.dd4t.databind.util.TypeUtils;
import org.dd4t.databind.viewmodel.base.ModelFieldMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * test
 *
 * @author R. Kempees
 * @since 19/11/14.
 */
public class JsonModelConverter extends AbstractModelConverter implements ModelConverter {
	private static final Logger LOG = LoggerFactory.getLogger(JsonModelConverter.class);
	private Class<org.dd4t.contentmodel.Field> concreteFieldImpl;

	public JsonModelConverter () {

	}

	@Override public <T extends BaseViewModel> T convertSource (final Object data, final T model) throws SerializationException {
		if (data == null) {
			LOG.debug("No data - nothing to do.");
			return null;
		}

		// TODO: can only be set here when JsonDataBinder has finally instantiated...
		this.concreteFieldImpl = JsonDataBinder.getInstance().getConcreteFieldImpl();
		JsonNode rawJsonData;
		if (data instanceof JsonNode) {
			rawJsonData = (JsonNode) data;
		} else {
			LOG.error("Dunno what you're trying to do, but we're doing Json here.");
			return null;
		}

		LOG.info("Conversion start.");

		if (model instanceof TridionViewModel) {
			LOG.debug("We have a Tridion view model. Setting additional properties");
			setTridionProperties((TridionViewModel) model, rawJsonData);
		}
		boolean isRootComponent = true;
		JsonNode contentFields = null;
		if (rawJsonData.has(Constants.COMPONENT_FIELDS)) {
			contentFields = rawJsonData.get(Constants.COMPONENT_FIELDS);
		}
		JsonNode metadataFields = null;
		if (rawJsonData.has(Constants.METADATA_FIELDS)) {
			metadataFields = rawJsonData.get(Constants.METADATA_FIELDS);
		}

		if (contentFields == null && metadataFields == null) {
			isRootComponent = false;
		}


		// TODO: resolve unchecked exception (Move the Annotation to dd4t-api)
		// TODO: mandatory but missing fields need their XPath set as well..
		final Map<String, ModelFieldMapping> modelProperties = (HashMap<String, ModelFieldMapping>) model.getModelProperties();

		try {
			for (Map.Entry<String, ModelFieldMapping> entry : modelProperties.entrySet()) {
				final String fieldName = entry.getKey();
				LOG.debug("Key:{}", fieldName);

				ModelFieldMapping m = entry.getValue();
				final JsonNode currentNode = getJsonNodeToParse(fieldName, rawJsonData, isRootComponent, contentFields, metadataFields, m);
				// Since we are now now going from modelproperty > fetch data, the data might actually be null
				if (currentNode != null) {

					this.buildField(model, fieldName, currentNode, m);

				}
			}
		} catch (IllegalAccessException | IOException e) {
			LOG.error("Error setting field!", e);
		}

		return model;
	}

	private static JsonNode getJsonNodeToParse (final String key, final JsonNode rawJsonData, final boolean isRootComponent, final JsonNode contentFields, final JsonNode metadataFields, final ModelFieldMapping m) {
		final JsonNode currentNode;
		if (isRootComponent) {
			if (m.getViewModelProperty().isMetadata()) {
				currentNode = metadataFields;
			} else {
				currentNode = contentFields;
			}
		} else {
			currentNode = rawJsonData;
		}

		if (currentNode != null) {
			if (isRootComponent) {
				return currentNode.get(key);
			}
			return currentNode;
		}
		return null;
	}

	private <T extends BaseViewModel> void buildField (final T model, final String fieldName, final JsonNode currentField, final ModelFieldMapping m) throws IllegalAccessException, SerializationException, IOException {

		final Field modelField = m.getField();

		// TODO: maybe add Collection<?>
		if (modelField.getType().equals(List.class)) {
			final Type parametrizedType = TypeUtils.getRuntimeTypeOfTypeParameter(modelField.getGenericType());
			LOG.debug("Interface check: " + TypeUtils.classIsViewModel((Class<?>) parametrizedType));
			if (TypeUtils.classIsViewModel((Class<?>) parametrizedType)) {
				// Deserialize in a STM
				// TODO:
				LOG.debug(currentField.toString());

			} else {
				deserializeGeneric(model, currentField, modelField);
			}

		} else if (TypeUtils.classIsViewModel(modelField.getType())) {
			checkTypeAndBuildModel(model, fieldName, currentField, modelField);

		} else {
			deserializeGeneric(model, currentField, modelField);
		}

	}

	private static <T extends BaseViewModel> void checkTypeAndBuildModel (final T model, final String fieldName, final JsonNode currentField, final Field modelField) throws SerializationException, IllegalAccessException {
		if (!model.getClass().equals(modelField.getType())) {
			LOG.debug("Building a model or Component for field:{}, type: {}", fieldName, modelField.getType().getName());
			final BaseViewModel strongModel = buildModelForField(currentField, modelField);
			modelField.set(model, strongModel);
		} else {
			LOG.error("Type for field type: {} is the same as the type for this view model: {}. This is NOT supported because of infinite loops. Work around this by creating a separate field type.", model.getClass().getCanonicalName(), modelField.getType().getCanonicalName());
		}
	}

	private static <T extends BaseViewModel> BaseViewModel buildModelForField (final JsonNode currentField, final Field f) throws SerializationException {
		final Class<T> modelClassToUse = (Class<T>) f.getType();
		final BaseViewModel strongModel = DataBindFactory.buildModel(currentField, modelClassToUse, "");
		final ViewModel viewModelParameters = (ViewModel) modelClassToUse.getAnnotation(ViewModel.class);
		if (viewModelParameters.setRawData()) {
			strongModel.setRawData(currentField.toString());
		}
		return strongModel;
	}

	private <T extends BaseViewModel> void deserializeGeneric (final T model, final JsonNode currentField, final Field f) throws IOException, IllegalAccessException {
		LOG.debug("Field Type: " + f.getType().getCanonicalName());
		final org.dd4t.contentmodel.Field renderedField = JsonUtils.renderComponentField(currentField, this.concreteFieldImpl);
		LOG.trace("Rendered Field is: {} ", renderedField.toString());
		LOG.debug("Field Type is: {}", f.getType().toString());
		setFieldValue(model, f, renderedField);
	}

	private <T extends BaseViewModel> void setTridionProperties (final TridionViewModel model, final JsonNode rawComponent) {
		model.setLastPublishDate(JsonUtils.getDateFromField(Constants.LAST_PUBLISHED_DATE, rawComponent));
		model.setLastModified(JsonUtils.getDateFromField(Constants.LAST_MODIFIED_DATE, rawComponent));
		model.setTcmUri(JsonUtils.getTcmUriFromField(Constants.ID, rawComponent));
	}
}
