package com.sdl.tridion.referenceimpl.siteedit;

import javax.servlet.ServletRequest;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.DynamicComponent;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.GenericPage;
import com.sdl.tridion.referenceimpl.constants.Constants;
import com.sdl.tridion.referenceimpl.util.ApplicationContextProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.util.TCMURI;

/**
 * 
 * The class, with static methods only, serves views and controllers with SiteEdit tags. The point
 * of bundling them is to keep all SiteEdit behaviour in a single point, and hence its strange JSON
 * codes accessible to non-Tridion specialized developers. 
 *
 * @author <a href="rogier.oudshoorn@capgemini.com">Rogier Oudshoorn</a>
 * @version $Revision: 12477 $
 */
public final class SiteEditService {
    /**
     * creating logger instance.
     */
    private static Logger logger = LoggerFactory.getLogger(SiteEditService.class);  
    /**
     * Instantiating SiteEditSettings.
     */
    private static SiteEditSettings settings;
    
    static {
        settings = (SiteEditSettings) ApplicationContextProvider.getBean("SiteEditSettings");
    }
    
    /**
     * String format used to create the Page-level SiteEdit tags.
     * 
     */
    
    private SiteEditService() {
        
    }
    /**
     * defining pageseformat.
     */
    private static String pageseformat = 
    "<!-- Page Settings: {" +
    "\"PageID\":\"%1$s\"," +              // page tcm uri
    "\"PageModified\":\"%2$tFT%2$tH:%2$tM:%2$tS\"," +        // page modified date (2012-04-05T17:33:02)
    "\"PageTemplateID\":\"%3$s\"," +      // page template tcm uri
    "\"PageTemplateModified\":\"%4$tFT%4$tH:%4$tM:%4$tS\"" + // page template modified date (2012-04-05T17:33:02)
    "} -->" + 
    "<script type=\"text/javascript\" language=\"javascript\" defer=\"defer\" src=\"%5$s\" id=\"tridion.siteedit\"></script>";
    
    /**
     * Generates siteEdit tag for given servlet request.
     * 
     * @param req Request for the page the tag belongs to.
     * @return String representing the JSON SiteEdit tag.
     */
    public static String generateSiteEditPageTag(ServletRequest req) {
        GenericPage page = (GenericPage) req.getAttribute(Constants.PAGE_MODEL_KEY);
        
        
        return generateSiteEditPageTag(page);        
    }
    
    /**
     * Support function, checking if SE is enabled at a generic level. 
     * 
     * @param req
     * @return
     */
    public static boolean isSiteEditEnabled(ServletRequest req) {

        return settings.isEnabled();
    }
    
    /**
     * Support function, checking if SE is enabled for the given item ID.
     * 
     * @param tcmPubId An ID of a Tridion item
     * 
     * @return whether the item belongs to a publication that has active SE in this web application
     */
    public static boolean isSiteEditEnabled(String tcmPubId, ServletRequest req) {
        try {
            
            TCMURI uri = new TCMURI(tcmPubId);
            
            if (settings.isEnabled() || settings.hasPubSE(uri.getPublicationId())) {
                return true;
            }                        
        } catch (Exception ex) {
            logger.error("Unable to get pubID from URI", ex);
        }
        
        return false;
    }
    
    /**
     * Generates SiteEdit tag for given page.
     * 
     * @param page Page the tag belongs to.
     * @return String representing the JSON SiteEdit tag..
     */
    public static String generateSiteEditPageTag(GenericPage page) {
        try {
            TCMURI uri = new TCMURI(page.getId());
            
            if (settings.isEnabled() || settings.hasPubSE(uri.getPublicationId())) {
              
                return String.format(pageseformat, 
                		page.getId(), 
                		page.getRevisionDate(),
                		page.getPageTemplate().getId(),
                		page.getPageTemplate().getRevisionDate(),
                		settings.getContentmanager()
                );
            }
        } catch (Exception ex) {
            logger.error("Unable to get pubID from URI", ex);            
        }
        
        return "";
    }
    
    /**
     * String format representing a Component-level SiteEdit tag.
     * 
     */
    private static String componentseformat =    
    "<!-- Start Component Presentation: {" +
    "\"ComponentID\" : \"%1$s\", " +               // component tcm uri
    "\"ComponentModified\" : \"%2$tFT%2$tH:%2$tM:%2$tS\", " +         // component modified date (2012-04-05T17:33:02)
    "\"ComponentTemplateID\" : \"%3$s\", " +       // component template id
    "\"ComponentTemplateModified\" : \"%4$tFT%4$tH:%4$tM:%4$tS\", " + // component template modified date (2012-04-05T17:33:02)
    "\"IsRepositoryPublished\" : %5$b" +           // is repository published (true if dynamic component template, false otherwise)
//    "%6$b" +                                       // is query based (true for a broker queried dcp, omit if component presentation is embedded on a page)
    "} -->";    
    
    /**
     * Generates a SiteEdit tag for a componentpresentation. It also needs to know which region it's in (for component
     * swapping) and the order of the page (for a true unique ID).
     * 
     * @param cp The componentpresentation to mark.
     * @param orderOnPage The number the componentpresentation has on the page.
     * @param region The region the componentpresentation is to be shown in.
     * @return String representing the JSON SiteEdit tag.
     * @throws Exception When a componentpresentation is given that can not be parsed into a SiteEdit tag.
     */
    public static String generateSiteEditComponentTag(ComponentPresentation cp, int orderOnPage, String region, ServletRequest req) 
    throws Exception {
        if (!(cp.getComponent() instanceof GenericComponent)) {
            throw new Exception("Cannot create siteEdit tags for non-generic component.");
        }
        
        if (!isSiteEditEnabled(cp.getComponent().getId(), req)) {
            return "";
        }
        
        // cast to generic in order to get access to revision date
        GenericComponent comp = (GenericComponent) cp.getComponent();
        
        return String.format(componentseformat, 
        		 comp.getId(),
        		 comp.getRevisionDate(),
        		cp.getComponentTemplate().getId(),
        		cp.getComponentTemplate().getRevisionDate(),
        		cp.isDynamic()
        );
    }
    
    
    private static String linkedcomponentseformat =    
    "<!-- Start Component Presentation: {" +
    "\"ID\" :  \"tcm:%1$d-%2$d\", " +              		 		// pub uri + component tcm uri    
    "\"ComponentID\" : \"tcm:%1$d-%2$d\", " +               	// pub uri + component tcm uri
    "\"ComponentModified\" : \"%3$tFT%3$tH:%3$tM:%3$tS\", " +   // component modified date (2012-04-05T17:33:02)
    "\"ComponentTemplateID\" : \"tcm:%1$d-%4$d-32\", " +       	// pub uri + component template id
    "\"IsRepositoryPublished\" : %5$b, " +           			// is repository published (true if dynamic component template, false otherwise)
    "\"IsQueryBased\" : %6$b" +                                 // is query based (true for a broker queried dcp, omit if component presentation is embedded on a page)
    "} -->";  
    
    /**
     * Creates SiteEditTag for a linkedComponent (or: queried component!).
     * @param comp
     * @param templateId
     * @return
     */
    public static String generateLinkedComponentTag(DynamicComponent dcp){
    	   	
    	return String.format(linkedcomponentseformat, 
    			dcp.getNativeDCP().getPublicationId(),
    			dcp.getNativeDCP().getComponentId(),
    			dcp.getRevisionDate(),
    			dcp.getNativeDCP().getComponentTemplateId(),
    			dcp.getNativeDCP().isDynamic(), 
    			true
       );
    }
    
    /**
     * String format representing a simple, non-multivalue SiteEdit field marking.
     */
    private static String fieldseformat = 
    "<!-- Start Component Field: {\"XPath\":\"%1$s\"} -->"; // xpath of the field
    
    /**
     * Function generates a fieldmarking for SiteEditable fields.
     */
    public static String generateSiteEditFieldMarking(Field field) {
    	if(field != null){
	        return String.format(fieldseformat, 
	        		field.getXPath()
	        );
    	}
    	return "";
    }
    
    
    /**
     * Function generates a fieldmarking for SiteEditable fields.
     */
    public static String generateSiteEditFieldMarking(Field field, int number) {
    	if(field != null){
	        return String.format(fieldseformat, 
	        		field.getXPath() + "["+number+"]"
	        );
    	}
    	return "";
    }    
}
