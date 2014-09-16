package org.dd4t.providers.impl;

import java.text.ParseException;

import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.providers.ComponentProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.broker.StorageException;
import com.tridion.dcp.ComponentPresentation;
import com.tridion.dcp.ComponentPresentationFactory;
import com.tridion.storage.ComponentMeta;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.ItemDAO;
import com.tridion.util.TCMURI;

public class BrokerComponentProvider implements ComponentProvider {
	protected static Logger logger = LoggerFactory.getLogger(BrokerComponentProvider.class);

	// default CT to use when finding CP
	private String defaultComponentTemplateUri;

	// default output to use when finding CP
	private String defaultOutputFormat;

	
	public String getDefaultOutputFormat() {
		return defaultOutputFormat;
	}

	public void setDefaultOutputFormat(String defaultOutputFormat) {
		this.defaultOutputFormat = defaultOutputFormat;
	}

	public String getDefaultComponentTemplateUri() {
		return defaultComponentTemplateUri;
	}

	public void setDefaultComponentTemplateUri(String defaultComponentTemplateUri) {
		this.defaultComponentTemplateUri = defaultComponentTemplateUri;
	}
	
	@Override
	public String getComponentXML(int componentId, int publicationId) throws StorageException, ItemNotFoundException{
		return getComponentXMLByTemplate(componentId, 0, publicationId);
	}

	@Override
	public String getComponentXMLByTemplate(int componentId, int templateId,
			int publicationId) throws StorageException, ItemNotFoundException{			
		
		ComponentPresentation cp = getDynamicComponentPresentation(componentId, templateId, publicationId);
		return cp.getContent();
	}

	@Override
	public ComponentMeta getComponentMeta(int componentId, int publicationId)
			throws StorageException {
		   ItemDAO itemDAO = (ItemDAO) StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.COMPONENT_META);
           return (ComponentMeta) itemDAO.findByPrimaryKey(publicationId, componentId);
	}

	@Override
	public ComponentPresentation getDynamicComponentPresentation(
			int componentId, int publicationId) throws StorageException, ItemNotFoundException{
		
		return getDynamicComponentPresentation(componentId, 0, publicationId);		
	}	
	
	@Override
	public ComponentPresentation getDynamicComponentPresentation(
			int componentId, int templateId, int publicationId)
			throws StorageException, ItemNotFoundException {
		
		ComponentPresentationFactory factory = new ComponentPresentationFactory(publicationId);
		ComponentPresentation cp = null;

		if (templateId != 0) {
			logger.debug("templateId requested");
				cp = factory.getComponentPresentation(
						componentId, templateId);
		
			if (cp == null) {
				logger.debug("component presentation NOT found by template");

				// no cp found, and they asked for a template, means we have to throw an exception
				throw new ItemNotFoundException("Unable to find DCP by template for pub "+publicationId+", item "+componentId+" and template "+templateId);	
			}
			
			logger.debug("component presentation found by template. ");
				
			return cp;
		}

		// option 1: use defaultComponentTemplateUri (if given)
		String defCT = this.getDefaultComponentTemplateUri();
		if (defCT != null && !defCT.equals("")) {
			logger.debug("defaultComponentTemplateUri is specified as "
					+ defCT);
			TCMURI defCTUri;
			try {
				defCTUri = new TCMURI(this.defaultComponentTemplateUri);
				cp = factory.getComponentPresentation(componentId, defCTUri.getItemId());				
			} catch (ParseException e) {
				logger.warn(
						"malformed defaultComponentTemplateUri in configuration",
						e);
			}
		}

		// option 2: use outputFormat (if given)
		if (cp == null) {
			// still no cp, try option 2:
			// use OutputFormat
			String defOF = this.getDefaultOutputFormat();
			if (defOF != null && !defOF.equals("")) {
				logger.debug("defaultOutputFormat is specified as " + defOF);

				cp = factory.getComponentPresentationWithOutputFormat(
						componentId, defOF);
			}
		}

		// option 3: nothing specified, just use priority
		if(cp == null){
			logger.debug("attempting to find component presentation on priority");

		    // then lets check priority
		    cp = factory.getComponentPresentationWithHighestPriority(componentId);
		}

		if (cp != null) {
			logger.debug("component presentation found");
			return cp;
		}		

		throw new ItemNotFoundException("Unable to find DCP for pub "+publicationId+", item "+componentId+" and template "+templateId);	
	}
}
