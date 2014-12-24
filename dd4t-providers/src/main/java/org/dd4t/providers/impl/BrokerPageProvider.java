package org.dd4t.providers.impl;

import com.tridion.broker.StorageException;
import com.tridion.data.CharacterData;
import com.tridion.meta.PublicationMetaFactory;
import com.tridion.storage.ItemMeta;
import com.tridion.storage.PageMeta;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ItemDAO;
import com.tridion.storage.dao.ItemTypeSelector;
import com.tridion.storage.dao.PageDAO;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.core.util.IOUtils;
import org.dd4t.core.util.TCMURI;
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
 */
public class BrokerPageProvider extends BaseBrokerProvider implements PageProvider {

	private final static Logger LOG = LoggerFactory.getLogger(BrokerPageProvider.class);
	private final static PublicationMetaFactory PUBLICATION_META_FACTORY = new PublicationMetaFactory();
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

	@Override public Boolean checkPageExists (final String url, final int publicationId) throws ItemNotFoundException, SerializationException {
		// TODO
		return false;
	}

	@Override public int discoverPublicationId (final String url) throws SerializationException {
		//TODO
		return -1;
	}
}
