package org.dd4t.providers.impl;

import java.io.IOException;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.core.util.IOUtils;
import org.dd4t.providers.PageProvider;

import com.tridion.broker.StorageException;
import com.tridion.data.CharacterData;
import com.tridion.storage.PageMeta;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ItemDAO;
import com.tridion.storage.dao.PageDAO;

public class BrokerPageProvider implements PageProvider {

	@Override
	public String getPageXMLByURL(String url, int publication) throws StorageException, IOException, ItemNotFoundException {
		PageMeta meta = getPageMetaByURL(url, publication);
		 
		if(meta == null)
			throw new ItemNotFoundException("Unable to find page by url '"+url+"' and publication '"+publication+"'.");
		
		return getPageXMLByMeta(meta);		 
	}

	@Override
	public String getPageXMLByMeta(PageMeta meta) throws StorageException,
			IOException {
		
		PageDAO pageDAO = (PageDAO) StorageManagerFactory.getDAO( meta.getPublicationId(), StorageTypeMapping.PAGE);
		
		 CharacterData data = pageDAO.findByPrimaryKey( meta.getPublicationId(),
				 meta.getItemId());
		 
		 if(data == null){
			 return null;
		 }
		 
		 return IOUtils.convertStreamToString(data.getInputStream());
	}

	@Override
	public PageMeta getPageMetaByURL(String url, int publication)
			throws StorageException, ItemNotFoundException {
		 ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);

		 PageMeta meta = (PageMeta) itemDAO.findByPageURL(publication, url);
			
		 if(meta == null)
			throw new ItemNotFoundException("Unable to find page by url '"+url+"' and publication '"+publication+"'.");
		
		 return meta;
	}

	@Override
	public PageMeta getPageMetaById(int id, int publication)
			throws StorageException, ItemNotFoundException {
		 ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publication, StorageTypeMapping.PAGE_META);
		 
		 PageMeta meta = (PageMeta) itemDAO.findByPrimaryKey(publication, id);
		 
		 if(meta == null)
			throw new ItemNotFoundException("Unable to find page by id '"+id+"' and publication '"+publication+"'.");
		
		 return meta;
	}

}
