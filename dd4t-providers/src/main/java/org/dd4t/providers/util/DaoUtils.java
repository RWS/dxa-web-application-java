package org.dd4t.providers.util;

import com.tridion.broker.StorageException;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BaseDAO;

/**
 * dd4t-2
 *
 * @author R. Kempees, M. Cadariu
 */
public class DaoUtils {
	public static BaseDAO getStorageDAO (int publicationId, StorageTypeMapping storageTypeMapping) throws StorageException {
		return StorageManagerFactory.getDAO(publicationId, storageTypeMapping);
	}

	private DaoUtils(){

	}
}