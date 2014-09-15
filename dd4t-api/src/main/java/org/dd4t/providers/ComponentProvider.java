package org.dd4t.providers;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;

import com.tridion.broker.StorageException;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.storage.ComponentMeta;

public interface ComponentProvider {		
	public String getComponentXML(int componentId, int publicationId) throws StorageException, ItemNotFoundException;
	
	public String getComponentXMLByTemplate(int componentId, int templateId, int publicationId) throws StorageException, ItemNotFoundException;
	
	public ComponentMeta getComponentMeta(int componentId, int publicationId) throws StorageException;

	public ComponentPresentation getDynamicComponentPresentation(int componentId, int templateId, int publicationId) throws StorageException, ItemNotFoundException;
	
	public ComponentPresentation getDynamicComponentPresentation(int componentId, int publicationId) throws StorageException, ItemNotFoundException;
}
