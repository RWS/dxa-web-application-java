package org.dd4t.providers.impl;

import com.tridion.broker.StorageException;
import com.tridion.broker.querying.MetadataType;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.dcp.ComponentPresentationFactory;
import com.tridion.storage.ComponentPresentationMeta;
import com.tridion.storage.CustomMetaValue;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ComponentPresentationMetaDAO;
import com.tridion.util.TCMURI;
import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.impl.DateField;
import org.dd4t.contentmodel.impl.NumericField;
import org.dd4t.contentmodel.impl.TextField;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.core.util.DateUtils;
import org.dd4t.providers.ComponentPresentationProvider;
import org.dd4t.providers.util.DaoUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Provides access to Dynamic Component Presentations stored in the Content Delivery database. It uses CD API to retrieve
 * raw DCP content from the database. Access to these objects is not cached, and as such must be cached externally.
 * TODO: we shouldn't throw exceptions if a DCP is not found?
 */
public class BrokerComponentPresentationProvider extends BaseBrokerProvider implements ComponentPresentationProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerComponentPresentationProvider.class);
	private static final Map<Integer, ComponentPresentationFactory> FACTORY_CACHE = new ConcurrentHashMap<>();
	private static final String ERROR_MESSAGE = "Component Presentation not found for componentId: %d, templateId: %d and publicationId: %d";

	private Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation;
	private Class<? extends ComponentTemplate> concreteComponentTemplateImpl;


	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId and publicationId.
	 * A templateId is not provided, so the DCP with the highest linking priority is retrieved.
	 * <p/>
	 * <b>Note: This method performs significantly slower than getDynamicComponentPresentation(int, int, int)!
	 * Do provide a templateId!</b>
	 *
	 * @param componentId   int representing the Component item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException if the requested DCP cannot be found
	 */
	@Override public org.dd4t.contentmodel.ComponentPresentation getDynamicComponentPresentation (int componentId, int publicationId) throws ItemNotFoundException, SerializationException {
		return getDynamicComponentPresentation(componentId, 0, publicationId);
	}

	/**
	 * Retrieves content of a Dynamic Component Presentation by looking up its componentId, templateId and publicationId.
	 *
	 * @param componentId   int representing the Component item id
	 * @param templateId    int representing the Component Template item id
	 * @param publicationId int representing the Publication id of the DCP
	 * @return String representing the content of the DCP
	 * @throws ItemNotFoundException if the requested DCP cannot be found
	 */
	@Override public org.dd4t.contentmodel.ComponentPresentation getDynamicComponentPresentation (int componentId, int templateId, int publicationId) throws ItemNotFoundException, SerializationException {
		ComponentPresentationFactory factory = FACTORY_CACHE.get(publicationId);
		int actualTemplateId = templateId;
		if (factory == null) {
			factory = new ComponentPresentationFactory(publicationId);
			FACTORY_CACHE.put(publicationId, factory);
		}

		ComponentPresentation result;
		String resultString;
		if (templateId != 0) {
			result = factory.getComponentPresentation(componentId, templateId);

			if (result == null) {
				LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
				throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
			}

			resultString = result.getContent();
		} else {
			result = factory.getComponentPresentationWithHighestPriority(componentId);
			if (result == null) {
				LOG.info(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
				throw new ItemNotFoundException(String.format(ERROR_MESSAGE, componentId, templateId, publicationId));
			}
			actualTemplateId = result.getComponentTemplateId();
			resultString = result.getContent();
		}

		if (!StringUtils.isEmpty(resultString)) {
			return constructComponentPresentation(decodeAndDecompressContent(resultString), publicationId, componentId, actualTemplateId, result);
		}
		return null;
	}

	/**
	 * Convenience method to obtain a list of component presentations for the same template id.
	 * <p/>
	 * TODO
	 *
	 * @param itemUris      array of found Component TCM IDs
	 * @param templateId    the CT Id to fetch DCPs on
	 * @param publicationId the current Publication Id
	 * @return a List of Component Presentations
	 * @throws org.dd4t.core.exceptions.ItemNotFoundException
	 * @throws org.dd4t.core.exceptions.SerializationException
	 */
	@Override public List<org.dd4t.contentmodel.ComponentPresentation> getDynamicComponentPresentations (final String[] itemUris, final int templateId, final int publicationId) throws ItemNotFoundException, SerializationException {
		return null;
	}


	private org.dd4t.contentmodel.ComponentPresentation constructComponentPresentation (String componentSource, int publicationId, int componentId, int componentTemplateId, ComponentPresentation componentPresentation) {
		try {

			final ComponentPresentationMetaDAO componentPresentationMetaDAO = (ComponentPresentationMetaDAO) DaoUtils.getStorageDAO(publicationId, StorageTypeMapping.COMPONENT_PRESENTATION_META);
			final ComponentPresentationMeta componentPresentationMeta = componentPresentationMetaDAO.findByPrimaryKey(publicationId, componentId, componentTemplateId);

			final org.dd4t.contentmodel.ComponentPresentation componentPresentationResult = this.concreteComponentPresentation.newInstance();
			final ComponentTemplate componentTemplate = this.concreteComponentTemplateImpl.newInstance();
			componentPresentationResult.setRawComponentContent(componentSource);
			componentPresentationResult.setIsDynamic(componentPresentation.isDynamic());
			componentTemplate.setId(new TCMURI(publicationId, componentTemplateId, 32, 0).toString());
			componentTemplate.setTitle(componentPresentationMeta.getTemplateMeta().getTitle());
			final DateTime dateTime = new DateTime(componentPresentationMeta.getTemplateMeta().getLastPublishDate());
			componentTemplate.setRevisionDate(dateTime);

			// Setting custommetavalues to get the viewname, among others
			final List<CustomMetaValue> customMetaValues = componentPresentationMeta.getTemplateMeta().getCustomMetaValues();

			final Map<String, Field> metadata = new HashMap<>();
			setTemplateMeta(customMetaValues, metadata);

			componentTemplate.setMetadata(metadata);

			componentPresentationResult.setComponentTemplate(componentTemplate);
			return componentPresentationResult;
		} catch (InstantiationException | StorageException | IllegalAccessException e) {
			LOG.error(e.getLocalizedMessage(), e);
		}
		return null;
	}


	private static void setTemplateMeta (final List<CustomMetaValue> customMetaValues, final Map<String, Field> metadata) {
		for (CustomMetaValue customMetaValue : customMetaValues) {

			Field field = null;

			if (metadata.containsKey(customMetaValue.getKeyName())) {
				field = metadata.get(customMetaValue.getKeyName());
			}
			final Object valueToSet;
			if (customMetaValue.getMetadataType() == MetadataType.DATE) {
				valueToSet = new DateTime(customMetaValue.getDateValue());
				if (field == null) {
					metadata.put(customMetaValue.getKeyName(), getDateField(customMetaValue.getKeyName(), (DateTime) valueToSet));
				} else {
					((DateField) field).getDateTimeValues().add(DateUtils.convertDateToString((DateTime) valueToSet));
				}
			} else if (customMetaValue.getMetadataType() == MetadataType.STRING) {
				valueToSet = customMetaValue.getStringValue();
				if (field == null) {
					metadata.put(customMetaValue.getKeyName(), getTextField(customMetaValue.getKeyName(), (String) valueToSet));
				} else {
					((DateField) field).getDateTimeValues().add((String) valueToSet);
				}

			} else if (customMetaValue.getMetadataType() == MetadataType.FLOAT) {
				valueToSet = customMetaValue.getFloatValue().intValue();
				if (field == null) {
					metadata.put(customMetaValue.getKeyName(), getNumericField(customMetaValue.getKeyName(), (int) valueToSet));
				} else {
					((NumericField) field).getNumericValues().add((double) valueToSet);
				}
			}
		}
	}

	private static Field getNumericField (String name, int value) {
		final NumericField numericField = new NumericField();
		numericField.setName(name);
		final List<Double> values = new ArrayList<>();
		values.add((double) value);
		numericField.setNumericValues(values);
		return numericField;
	}

	private static Field getDateField (String name, DateTime value) {
		final DateField dateField = new DateField();
		dateField.setName(name);
		final List<String> values = new ArrayList<>();
		values.add(DateUtils.convertDateToString(value));
		dateField.setDateTimeValues(values);
		return dateField;
	}

	private static Field getTextField (String name, String value) {
		final TextField textField = new TextField();
		textField.setName(name);
		final List<String> values = new ArrayList<>();
		values.add(value);
		textField.setTextValues(values);
		return textField;
	}

	public void setConcreteComponentPresentation (final Class<? extends org.dd4t.contentmodel.ComponentPresentation> concreteComponentPresentation) {
		this.concreteComponentPresentation = concreteComponentPresentation;
	}

	public void setConcreteComponentTemplateImpl (final Class<? extends ComponentTemplate> concreteComponentTemplateImpl) {
		this.concreteComponentTemplateImpl = concreteComponentTemplateImpl;
	}
}
