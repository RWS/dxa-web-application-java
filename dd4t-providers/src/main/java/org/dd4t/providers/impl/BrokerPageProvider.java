package org.dd4t.providers.impl;

import com.tridion.broker.StorageException;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.content.PageURLCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.criteria.operators.AndCriteria;
import com.tridion.broker.querying.filter.LimitFilter;
import com.tridion.broker.querying.sorting.SortDirection;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.data.CharacterData;
import com.tridion.dynamiccontent.DynamicMetaRetriever;
import com.tridion.meta.PublicationMetaFactory;
import com.tridion.storage.ItemMeta;
import com.tridion.storage.PageMeta;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ItemDAO;
import com.tridion.storage.dao.ItemTypeSelector;
import com.tridion.storage.dao.PageDAO;
import org.dd4t.core.caching.CacheElement;
import org.dd4t.core.caching.CacheType;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.factories.impl.CacheProviderFactoryImpl;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.core.util.IOUtils;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.CacheProvider;
import org.dd4t.providers.PageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

/**
 * Provides access to Page content and metadata from Content Delivery database. Access to page content is not cached,
 * so as such much be cached externally. Calls to Page meta are cached in the Tridion object cache.
 *
 * TODO: Logging, use this class only from the enum TridionBrokerPageProvider
 * TODO: Implement caching
 * TODO: Singleton through enum
 */
public class BrokerPageProvider extends BaseBrokerProvider implements PageProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerPageProvider.class);
	private static final  PublicationMetaFactory PUBLICATION_META_FACTORY = new PublicationMetaFactory();
	private final CacheProvider cacheProvider = CacheProviderFactoryImpl.getInstance().getCacheProvider();
	private static final DynamicMetaRetriever DYNAMIC_META_RETRIEVER = new DynamicMetaRetriever();
	/**
	 * Retrieves content of a Page by looking the page up by its item id and Publication id.
	 *
	 * @param id          int representing the page item id
	 * @param publication int representing the Publication id of the page
	 * @return String representing the content of the Page
	 * @throws StorageException      if something went wrong while accessing the CD DB
	 * @throws IOException           if the character stream cannot be read
	 * @throws ItemNotFoundException if the requested page does not exist
	 */
	@Override
	public String getPageContentById (int id, int publication) throws IOException, ItemNotFoundException, SerializationException {

		CharacterData data = null;
		try {
			PageDAO pageDAO = (PageDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE);
			data = pageDAO.findByPrimaryKey(publication, id);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}

		if (data == null) {
			throw new ItemNotFoundException("Unable to find page by id '" + id + "' and publication '" + publication + "'.");
		}
		String result = decodeAndDecompressContent(IOUtils.convertStreamToString(data.getInputStream()));
		return result;
	}

	/**
	 * Retrieves content of a Page by looking the page up by its URL.
	 *
	 * @param url         String representing the path part of the page URL
	 * @param publication int representing the Publication id of the page
	 * @return String representing the content of the Page
	 * @throws StorageException      if something went wrong while accessing the CD DB
	 * @throws IOException           if the character stream cannot be read
	 * @throws ItemNotFoundException if the requested page does not exist
	 */
	@Override
	public String getPageContentByURL(String url, int publication) throws ItemNotFoundException, IOException, SerializationException {
		PageMeta meta = getPageMetaByURL(url, publication);

		if (meta == null) {
			throw new ItemNotFoundException("Unable to find page by url '" + url + "' and publication '" + publication + "'.");
		}

		return getPageContentById(meta.getItemId(), meta.getPublicationId());
	}

	@Override public String getPageContentById (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException, IOException {
		TCMURI uri = new TCMURI(tcmUri);
		return getPageContentById(uri.getItemId(),uri.getPublicationId());
	}

	/**
	 * Retrieves metadata of a Page by looking the page up by its item id and Publication id.
	 *
	 * @param id          int representing the page item id
	 * @param publication int representing the Publication id of the page
	 * @return PageMeta representing the metadata of the Page
	 * @throws StorageException      if something went wrong while accessing the CD DB
	 * @throws ItemNotFoundException if the requested page does not exist
	 */
	public PageMeta getPageMetaById(int id, int publication) throws ItemNotFoundException {

		PageMeta meta = null;
		try {
			ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
			meta = (PageMeta) itemDAO.findByPrimaryKey(publication, id);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}

		if (meta == null) {
			throw new ItemNotFoundException("Unable to find page by id '" + id + "' and publication '" + publication + "'.");
		}

		return meta;
	}

	/**
	 * Retrieves metadata of a Page by looking the page up by its URL.
	 *
	 * @param url         String representing the path part of the page URL
	 * @param publication int representing the Publication id of the page
	 * @return PageMeta representing the metadata of the Page
	 * @throws StorageException      if something went wrong while accessing the CD DB
	 * @throws ItemNotFoundException if the requested page does not exist
	 */
	public PageMeta getPageMetaByURL(String url, int publication) throws ItemNotFoundException {

		PageMeta meta = null;
		try {
			ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
			meta = itemDAO.findByPageURL(publication, url);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}

		if (meta == null) {
			throw new ItemNotFoundException("Unable to find page by url '" + url + "' and publication '" + publication + "'.");
		}

		return meta;
	}

	/**
	 * Retrieves a list of URLs for all published Tridion Pages in a Publication.
	 *
	 * @param publication int representing the Publication id of the page
	 * @return String representing the list of URLs (one URL per line)
	 * @throws ItemNotFoundException if the requested page does not exist
	 * @throws StorageException      if something went wrong while accessing the CD DB
	 */
	@Override
	public String getPageListByPublicationId(int publication) throws ItemNotFoundException {

		List<ItemMeta> itemMetas = null;
		try {
			ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
			itemMetas = itemDAO.findAll(publication, ItemTypeSelector.PAGE);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}

		if (itemMetas == null || itemMetas.size() == 0) {
			throw new ItemNotFoundException("Unable to find page URL list by publication '" + publication + "'.");
		}

		StringBuilder result = new StringBuilder();
		for (ItemMeta itemMeta : itemMetas) {
			result.append(((PageMeta) itemMeta).getUrl() + "\r\n");
		}

		return result.toString();
	}

	// TODO: introduce ProviderException
	@Override public Boolean checkPageExists (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {

		LOG.debug("Checking whether Page with url: {} exists", url);

		String key = getKey(CacheType.PageExists, url);
		CacheElement<Integer> cacheElement = cacheProvider.loadFromLocalCache(key);
		Integer result = null;

		if (cacheElement.isExpired()) {
			synchronized (cacheElement) {
				if (cacheElement.isExpired()) {
					cacheElement.setExpired(false);
					final PublicationCriteria publicationCriteria = new PublicationCriteria(publicationId);
					final PageURLCriteria pageURLCriteria = new PageURLCriteria(url);

					final Query tridionQuery = new Query(new AndCriteria(publicationCriteria, pageURLCriteria));
					tridionQuery.setResultFilter(new LimitFilter(1));
					tridionQuery.addSorting(new SortParameter(SortParameter.ITEMS_URL, SortDirection.DESCENDING));

					try {
						String[] results = tridionQuery.executeQuery();
						if (results != null && results.length > 0) {
							LOG.debug("Found 1 result");
							result = 1;
							TCMURI tcmuri = new TCMURI(results[0]);
							cacheElement.setPayload(result);
							cacheProvider.storeInItemCache(key, cacheElement, tcmuri.getPublicationId(), tcmuri.getItemId());
						} else {
							LOG.debug("No results");
							result = 0;
							cacheElement.setPayload(result);
							cacheProvider.storeInItemCache(key, cacheElement);
						}
					} catch (StorageException | ParseException e) {
						LOG.error(e.getLocalizedMessage(),e);
					}
					LOG.debug("Stored Page exist check with key: {} in cache", key);
				} else {
					LOG.debug("Fetched a Page exist check with key: {} from cache", key);
					result = cacheElement.getPayload();
				}
			}
		} else {
			LOG.debug("Fetched Page exist check with key: {} from cache", key);
			result = cacheElement.getPayload();
		}

		return result != null && (result == 1);
	}

	@Override public int discoverPublicationId (final String url) throws SerializationException {
		LOG.debug("Discovering Publication id for url: {}", url);
		final String key = getKey(CacheType.DiscoverPublicationURL, url);
		final CacheElement<Integer> cacheElement = cacheProvider.loadFromLocalCache(key);
		Integer result = -1;

		if (cacheElement.isExpired()) {
			synchronized (cacheElement) {
				if (cacheElement.isExpired()) {
					cacheElement.setExpired(false);

//					String[] urlsToTry = new String[2];
//					if (url.endsWith("/")) {
//						urlsToTry[0] = url;
//						urlsToTry[1] = url.substring(0, url.length() - 1);
//					} else {
//						urlsToTry[0] = url + "/";
//						urlsToTry[1] = url;
//					}

					final com.tridion.meta.PageMeta pageMeta = DYNAMIC_META_RETRIEVER.getPageMetaByURL(url);
					if (pageMeta != null) {
						result = pageMeta.getPublicationId();
						LOG.debug("Publication Id for URL: {}, is {}", url,result);
					} else {
						LOG.warn("Could not resolve publication Id for URL: {}",url);
					}

//					for (String urlToTry : urlsToTry) {
//						PublicationMeta[] pms = new PublicationMeta[0];
//						try {
//							pms = PUBLICATION_META_FACTORY.getMetaByPublicationUrl(urlToTry);
//						} catch (StorageException e) {
//							throw new SerializationException(e);
//						}
//
//						if (pms.length > 0) {
//							result = pms[0].getId();
//							if (pms.length > 1) {
//								LOG.error("Found duplicate Publication IDs, returning the first publication: {}", result);
//							}
//							LOG.debug("Discovered Publication id {}", result);
//							break;
//						}
//					}

					cacheElement.setPayload(result);
					cacheProvider.storeInItemCache(key, cacheElement);
					LOG.debug("Stored Publication Id with key: {} in cache", key);
				} else {
					LOG.debug("Fetched a Publication Id with key: {} from cache", key);
					result = cacheElement.getPayload();
				}
			}
		} else {
			LOG.debug("Fetched Publication Id with key: {} from cache", key);
			result = cacheElement.getPayload();
		}

		return result == null ? -1 : result;
	}

	/**
	 * Builds a key using a named cache type (region) and a URL. This type of key is used to point to
	 * actual payload in the cache. Use this key when looking up objects cached for a particular URL.
	 *
	 * @param type CacheType representing the type (or region) where the associated item is in cache
	 * @param url  the path part of the URL of a Tridion item
	 * @return String representing the key pointing to a URL value
	 */
	private String getKey(CacheType type, String url) {
		return String.format("%s-%s", type, url);
	}
}
