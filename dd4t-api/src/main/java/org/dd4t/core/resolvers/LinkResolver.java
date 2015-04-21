package org.dd4t.core.resolvers;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;

public interface LinkResolver {

	public String resolve(Component component, Page page) throws SerializationException, ItemNotFoundException;

	public String resolve(Component component) throws ItemNotFoundException, SerializationException;
	
	public String resolve(ComponentPresentation cp) throws ItemNotFoundException, SerializationException;

	public String resolve(String componentId) throws SerializationException, ItemNotFoundException;

	public String resolve(String componentId, String pageId) throws ItemNotFoundException, SerializationException;

	public String getContextPath();

	public void setContextPath(String contextPath);
}
