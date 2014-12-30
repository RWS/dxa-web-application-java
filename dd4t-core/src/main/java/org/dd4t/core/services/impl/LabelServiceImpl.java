package org.dd4t.core.services.impl;

import org.apache.commons.lang3.StringUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.factories.impl.PropertiesServiceFactory;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.TCMURI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Specific implementation of the LabelServiceBase class, which defines the load() method in charge with loading a
 * label map for a given Publication. The Page must contain one CP of type Label and it's
 * loading the specific model 'Label' that contains the key-value label pairs.
 *
 * The class is a singleton instantiated by Spring context that also defines autowired CacheProvider and
 * PublicationResolver.
 *
 * @author Mihai Cadariu
 */
public class LabelServiceImpl extends LabelServiceBase {

	private static final Logger LOG = LoggerFactory.getLogger(LabelServiceImpl.class);
	private final static String PUBLICATION_BY_LEGACY_ID_PREFIX = "pub-by-legacy-";
	private final String labelPageURL;
	private PageFactory pageFactory;

	/**
	 * Private singleton constructor reads the property LabelPageURL from file 'dd4t.properties', which defines
	 * the Tridion Page URL that contains the Label CP.
	 */
	private LabelServiceImpl () {
		LOG.debug("Create new instance");
		PropertiesServiceFactory factory = PropertiesServiceFactory.getInstance();
		PropertiesService service = factory.getPropertiesService();
		labelPageURL = service.getProperty(Constants.LABELPAGE_URL);
		LOG.debug("Using labelpage.url=" + labelPageURL);
	}

	/**
	 * Loads the labels key, value pairs for the given Publication. It works with the cache as well, storing newly
	 * loaded map values into the cache. The returned Map represents all the labels for the given Publication context.
	 * <p/>
	 * The given Publication context will trigger the loading of the Labels Tridion Page in the given context, such
	 * that localized versions of the labels are possible.
	 *
	 * @param publicationId int representing the context Publication to load labels for
	 * @return Map with String keys and String values representing the key, value pairs of localized labels for the
	 * given Publication
	 * @throws java.io.IOException if something went wrong with loading the values from Tridion
	 */
	@Override public Map<String, String> load (int publicationId) throws IOException {
		String mapKey = getMapKey(publicationId);
		CacheElement<Map<String, String>> cacheElement = cacheProvider.loadFromLocalCache(mapKey);
		Map<String, String> result;

		if (cacheElement.isExpired()) {
			cacheElement.setExpired(false);
			result = new TreeMap<>();

			try {
				String url = publicationResolver.getLocalPageUrl(labelPageURL);
				if (StringUtils.isEmpty(url)) {
					// url is not valid, throw an ItemNotFoundException
					throw new ItemNotFoundException("Local Page Url could not be resolved: " + labelPageURL + " (probably publication url could not be resolved)");
				}
				Page labelPage = (Page) pageFactory.findPageByUrl(url, publicationResolver.getPublicationId());

				List<ComponentPresentation> componentPresentations = labelPage.getComponentPresentations();
				if (componentPresentations.size() == 0) {
					throw new ItemNotFoundException("Label Page must contain at least one Component Presentation " + labelPage.getId());
				}

				for (ComponentPresentation componentPresentation : componentPresentations) {
					Component labelComponent = componentPresentation.getComponent();
// TODO:
//					BaseModel baseModel = ModelFactory.createInstance(labelComponent);
//					if (baseModel instanceof Label) {
//						Label label = (Label) baseModel;
//						for (EmbeddedLabel embeddedLabel : label.getLabelList()) {
//							String key = embeddedLabel.getLKey();
//							String value = embeddedLabel.getLValue();
//							result.put(key, value);
//							LOG.debug("Loading " + mapKey + " with {} --> {}", key, value);
//						}
//					} else {
//						LOG.error("Found Component Presentation on label Page that is not a Label Component! Skipping...");
//					}
				}

				TCMURI tcmUri = new TCMURI(labelPage.getId());
				cacheElement.setPayload(result);
				cacheProvider.storeInItemCache(mapKey, cacheElement, publicationId, tcmUri.getItemId());
				LOG.debug("Added label map for publicationId: {} to the cache", publicationId);
			} catch (ItemNotFoundException e) {
				LOG.error("Cannot load label page " + labelPageURL + " for publication " + publicationId +
						". Message: " + e.getMessage());
			} catch (ParseException | FactoryException e) {
				LOG.error("Exception occurred while loading the label map", e);
				throw new IOException("Failed to load labels", e);
			}
		} else {
			LOG.debug("Fetched Label map with key: {} from cache", mapKey);
			result = cacheElement.getPayload();
		}

		return result;
	}

	public int getPublicationByLegacyId (int legacyId) {
		String key = PUBLICATION_BY_LEGACY_ID_PREFIX + legacyId;
		try {
			String value = getLabel(key);
			return Integer.parseInt(value);
		} catch (IOException e) {
			LOG.warn("Error getting label {}, returning 0", key);
			return 0;
		}
	}


	@Autowired public void setPageFactory (PageFactory factory) {
		LOG.debug("Set PageFactory " + factory);
		this.pageFactory = factory;
	}
}
