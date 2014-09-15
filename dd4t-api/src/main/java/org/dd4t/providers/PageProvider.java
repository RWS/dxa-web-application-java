package org.dd4t.providers;

import java.io.IOException;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;

import com.tridion.broker.StorageException;
import com.tridion.storage.PageMeta;

public interface PageProvider {
	public PageMeta getPageMetaByURL(String url, int publication) throws StorageException, ItemNotFoundException;
	
	public PageMeta getPageMetaById(int id, int publication) throws StorageException, ItemNotFoundException;
	
	public String getPageXMLByMeta(PageMeta meta) throws StorageException, IOException;
	
	public String getPageXMLByURL(String url, int publication) throws StorageException, IOException, ItemNotFoundException;
}
