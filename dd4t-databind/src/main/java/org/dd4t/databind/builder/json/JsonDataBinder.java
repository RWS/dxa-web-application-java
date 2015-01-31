package org.dd4t.databind.builder.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.joda.JodaModule;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.*;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.databind.TridionViewModel;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.TCMURI;
import org.dd4t.databind.builder.BaseDataBinder;
import org.dd4t.databind.serializers.json.BaseFieldMixIn;
import org.dd4t.databind.serializers.json.ComponentPresentationDeserializer;
import org.dd4t.databind.util.DataBindConstants;
import org.dd4t.databind.util.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.text.ParseException;
import java.util.*;

/**
 * @author R. Kempees
 * @since 17/11/14.
 */
public class JsonDataBinder extends BaseDataBinder implements DataBinder {
	private static final Logger LOG = LoggerFactory.getLogger(JsonDataBinder.class);
	private static final JsonDataBinder INSTANCE = new JsonDataBinder();
	private static final ObjectMapper GENERIC_MAPPER = new ObjectMapper();

	static {
		GENERIC_MAPPER.registerModule(new JodaModule());
		GENERIC_MAPPER.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
	}

	private JsonDataBinder () {
		LoggerFactory.getLogger(JsonDataBinder.class).info("Creating a JsonDataBinder instance.");
	}

	public static JsonDataBinder getInstance () {
		return INSTANCE;
	}

	public <T extends Page> T buildPage (final String source, final Class<T> aClass) throws SerializationException {
		try {
			return GENERIC_MAPPER.readValue(source, aClass);
		} catch (IOException e) {
			LOG.error(DataBindConstants.MESSAGE_ERROR_DESERIALIZING, e);
			throw new SerializationException(e);
		}
	}

	public <T extends ComponentPresentation> T buildDynamicComponentPresentation (final String source, final Class<T> aClass) throws SerializationException {
		try {
			return GENERIC_MAPPER.readValue(source, aClass);
		} catch (IOException e) {
			LOG.error(DataBindConstants.MESSAGE_ERROR_DESERIALIZING, e);
			throw new SerializationException(e);
		}
	}

	public <T extends Component> T buildComponent (final Object source, final Class<T> aClass) throws SerializationException {
		try {
			if (source instanceof JsonNode) {
				final JsonParser parser = ((JsonNode) source).traverse();
				return GENERIC_MAPPER.readValue(parser, aClass);
			} else if (source instanceof String) {
				return GENERIC_MAPPER.readValue((String) source, aClass);
			} else {
				LOG.error("Cannot parse type: " + source.getClass().toString());
				return null;
			}

		} catch (IOException e) {
			LOG.error(DataBindConstants.MESSAGE_ERROR_DESERIALIZING, e);
			throw new SerializationException(e);
		}
	}

	public HashMap<String, BaseViewModel> buildModels (final Object source, final Set<String> modelNames, final String templateUri) throws SerializationException {

		final HashMap<String, BaseViewModel> models = new HashMap<>();

		for (String modelName : modelNames) {
			if (VIEW_MODELS.containsKey(modelName)) {
				final Class modelClass = VIEW_MODELS.get(modelName);
				// check to ensure we don't already have built the same model. We can reuse it if the case
				// this loop is cheaper than deserializing new models all the time
				final BaseViewModel alreadyExistingModel = getModelOrNullForExistingEntry(models, modelClass);
				if (alreadyExistingModel != null) {
					models.put(modelName, alreadyExistingModel);
				} else {
					models.put(modelName, buildModel(source, modelClass, templateUri));
				}
			} else {
				LOG.warn("Could not load Model Class for key: {}", modelName);
			}
		}
		return models;
	}

	/**
	 * @param source      the source object. In this case a JsonNode
	 * @param modelName   the viewModel name set on the CT
	 * @param templateUri the CT URI. Just to set it on the model
	 * @param <T>         Any mode deriving from BaseViewModel
	 * @return A concrete instance of the view model.
	 * @throws SerializationException
	 */
	public <T extends BaseViewModel> T buildModel (final Object source, final String modelName, final String templateUri) throws SerializationException {
		if (VIEW_MODELS.containsKey(modelName)) {
			Class modelClass = VIEW_MODELS.get(modelName);
			LOG.info("Start building model for viewName: {}, with class: {}", modelName,modelClass);
			return buildModel(source, modelClass, templateUri);
		}
		LOG.info("Could not load Model Class for viewName: {}", modelName);
		return null;
	}

	public <T extends BaseViewModel> T buildModel (final Object source, final Class modelClass, final String templateUri) throws SerializationException {

		try {
			// TODO:If anyone can resolve this unchecked cast, then great.
			// This appears a limitation in the Java Generics implementation.
			final T concreteModel = (T) modelClass.newInstance();

			if (concreteModel instanceof TridionViewModel && !StringUtils.isEmpty(templateUri)) {
				((TridionViewModel) concreteModel).setTemplateUri(new TCMURI(templateUri));
			}

			LOG.debug("Building model {}", concreteModel.getClass().toString());
			return this.converter.convertSource(source, concreteModel);
		} catch (InstantiationException | IllegalAccessException | ParseException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return null;
	}

	public static ObjectMapper getGenericMapper () {
		return GENERIC_MAPPER;
	}

	@PostConstruct @Override protected void init () {

		this.configureMapper();
		this.checkViewModelConfiguration();
		this.scanAndLoadModels();
	}

	protected void configureMapper () {
		// This is the hook where view models are custom generated
		final ComponentPresentationDeserializer componentPresentationDeserializer = new ComponentPresentationDeserializer(this.concreteComponentPresentationImpl, this.concreteComponentTemplateImpl, this.concreteComponentImpl);
		final SimpleModule module = new SimpleModule("ComponentPresentationDeserializerModule", new Version(1, 0, 0, "RELEASE", "org.dd4t", "dd4t-databind"));
		module.addDeserializer(ComponentPresentation.class, componentPresentationDeserializer);
		GENERIC_MAPPER.registerModule(module);
		GENERIC_MAPPER.addMixInAnnotations(Field.class, BaseFieldMixIn.class);

		LOG.debug("Mapper configured for: {} and {}", this.concreteComponentPresentationImpl.toString(), this.concreteComponentTemplateImpl.toString());
	}

	public String findComponentTemplateViewName (ComponentTemplate template) throws IOException {
		if (template == null) {
			throw new IOException("The component template to find the viewModel of is null.");
		}

		final Map<String, Field> metaData = template.getMetadata();
		if (metaData != null && metaData.containsKey(JsonDataBinder.getInstance().viewModelMetaKeyName)) {

			Field viewNameField = metaData.get(JsonDataBinder.getInstance().viewModelMetaKeyName);
			if (viewNameField != null) {
				List<Object> values = viewNameField.getValues();
				if (!values.isEmpty()) {
					return (String) viewNameField.getValues().get(0);
				}
			}
		}
		return null;
	}

	public String getRootElementName (Object componentNode) {

		if (!JsonUtils.isValidJsonNode(componentNode)) {
			LOG.error("Dunno what you're trying to do, but we're doing Json here.");
			return null;
		}

		final JsonNode node = (JsonNode) componentNode;
		final JsonNode schemaNode = node.get(DataBindConstants.SCHEMA_NODE_NAME);
		if (schemaNode != null) {
			String nodeTypeName;
			if (schemaNode.hasNonNull(DataBindConstants.ROOT_ELEMENT_NAME)) {
				nodeTypeName = schemaNode.get(DataBindConstants.ROOT_ELEMENT_NAME).textValue();
				LOG.debug("RootElementName is: {}", nodeTypeName);
				return nodeTypeName;
			}
		}
		return null;
	}
}
