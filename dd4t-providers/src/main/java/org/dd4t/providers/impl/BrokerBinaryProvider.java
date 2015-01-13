package org.dd4t.providers.impl;

import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.BinaryMetaFactory;
import com.tridion.storage.BinaryContent;
import com.tridion.storage.BinaryVariant;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BinaryContentDAO;
import com.tridion.storage.dao.BinaryVariantDAO;
import org.dd4t.contentmodel.Binary;
import org.dd4t.contentmodel.impl.BinaryDataImpl;
import org.dd4t.contentmodel.impl.BinaryImpl;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.providers.BaseBrokerProvider;
import org.dd4t.core.util.TCMURI;
import org.dd4t.providers.BinaryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;

/**
 * Provides access to Binaries stored in the Content Delivery database. It uses JPA DAOs to retrieve raw binary content
 * or binary metadata from the database. Access to these objects is not cached, and as such must be cached externally.
 */
public class BrokerBinaryProvider extends BaseBrokerProvider implements BinaryProvider {

	private static final BinaryMetaFactory BINARY_META_FACTORY = new BinaryMetaFactory();

	private static final Logger LOG = LoggerFactory.getLogger(BrokerBinaryProvider.class);

	@Override public Binary getBinaryByURI (final String tcmUri) throws ItemNotFoundException, ParseException, SerializationException {
		final TCMURI binaryUri = new TCMURI(tcmUri);
		final BinaryMeta binaryMeta = BINARY_META_FACTORY.getMeta(tcmUri);
		return getBinary(binaryUri,binaryMeta);
	}


	@Override public Binary getBinaryByURL (final String url, final int publication) throws ItemNotFoundException, SerializationException {
		final BinaryMeta binaryMeta = BINARY_META_FACTORY.getMetaByURL(publication,url);
		final TCMURI binaryUri = new TCMURI(binaryMeta.getPublicationId(),TCMURI.safeLongToInt(binaryMeta.getId()), ItemTypes.COMPONENT,-1);
		return getBinary(binaryUri,binaryMeta);
	}

	private static Binary getBinary (final TCMURI binaryUri, final BinaryMeta binaryMeta) throws ItemNotFoundException {
		if (binaryMeta != null) {
			final BinaryImpl binary = new BinaryImpl();
			binary.setId(binaryUri.toString());
			binary.setUrlPath(binaryMeta.getURLPath());
			// TODO: check if this actually is the Mime Type
			binary.setMimeType(binaryMeta.getType());

			// TODO: binaryMeta.getCustomMeta();
			//binaryMeta.getDescription();
			//binaryMeta.getPath();
			//binaryMeta.getVariantId();


			final BinaryContentDAO contentDAO;
			BinaryContent content = null;
			try {
				contentDAO = (BinaryContentDAO) StorageManagerFactory.getDAO(binaryUri.getPublicationId(), StorageTypeMapping.BINARY_CONTENT);
				content = contentDAO.findByPrimaryKey(binaryUri.getPublicationId(), binaryUri.getItemId(), null);
			} catch (StorageException e) {
				LOG.error(e.getMessage(), e);
			}


			if (content == null) {
				throw new ItemNotFoundException("Unable to find binary content by id:" + binaryUri.toString());
			}

			final BinaryDataImpl binaryData = new BinaryDataImpl();
			binaryData.setBytes(content.getContent().clone());
			binary.setBinaryData(binaryData);
			return binary;
		}
		return null;
	}

	/**
	 * Retrieves the byte array content of a Tridion binary based on its TCM item id and publication id.
	 *
	 * @param id          int representing the item id
	 * @param publication int representing the publication id
	 * @return byte[] the byte array of the binary content
	 * @throws ItemNotFoundException if the item identified by id and publication was not found
	 */
	@Override
	public byte[] getBinaryContentById (int id, int publication) throws ItemNotFoundException {
		BinaryContentDAO contentDAO;
		BinaryContent content = null;
		try {
			contentDAO = (BinaryContentDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.BINARY_CONTENT);
			content = contentDAO.findByPrimaryKey(publication, id, null);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}


		if (content == null) {
			throw new ItemNotFoundException("Unable to find binary content by id '" + id + "' and publication '" + publication + "'.");
		}

		return content.getContent();
	}

	/**
	 * Retrieves the byte array content of a Tridion binary based on its URL.
	 *
	 * @param url         string representing the path portion of the URL of the binary
	 * @param publication int representing the publication id
	 * @return byte[] the byte array of the binary content
	 * @throws ItemNotFoundException if the item identified by id and publication was not found
	 */
	@Override
	public byte[] getBinaryContentByURL (String url, int publication) throws ItemNotFoundException {

		BinaryVariant variant = getBinaryVariantByURL(url, publication);
		return getBinaryContentById(variant.getBinaryId(), variant.getPublicationId());
	}

	/**
	 * Not in the interface just to keep the tridion dependency out.
	 * @param id          int representing the item id
	 * @param publication int representing the publication id
	 * @return BinaryVariant the binary identified by id and publication
	 * @throws ItemNotFoundException if the item identified by id and publication was not found
	 */

	public BinaryVariant getBinaryVariantById(int id, int publication) throws ItemNotFoundException {

		BinaryVariant variant = null;
		try {
			BinaryVariantDAO variantDAO = (BinaryVariantDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.BINARY_VARIANT);
			variant = variantDAO.findByPrimaryKey(publication, id, null);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}

		if (variant == null) {
			throw new ItemNotFoundException("Unable to find binary by id '" + id + "' and publication '" + publication + "'.");
		}

		return variant;
	}

	/**
	 * Not in the interface just to keep the tridion dependency out.
	 * @param url         string representing the path portion of the URL of the binary
	 * @param publication int representing the publication id
	 * @return BinaryVariant the binary identified by url and publication
	 * @throws ItemNotFoundException if the item identified by url and publication was not found
	 */

	public BinaryVariant getBinaryVariantByURL(String url, int publication) throws ItemNotFoundException {

		BinaryVariant variant = null;
		try {
			BinaryVariantDAO variantDAO = (BinaryVariantDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.BINARY_VARIANT);
			variant = variantDAO.findByURL(publication, url);
		} catch (StorageException e) {
			LOG.error(e.getMessage(),e);
		}

		if (variant == null) {
			throw new ItemNotFoundException("Unable to find binary by url '" + url + "' and publication '" + publication + "'.");
		}

		return variant;
	}
}
