package org.dd4t.springmvc.siteedit;

import java.util.List;

/**
 * This class is a settings container, suitable to be injected using Spring Dependency Injection.
 *
 * @author <a href="rogier.oudshoorn@capgemini.com">Rogier Oudshoorn</a>
 * @version $Revision: 12477 $
 */
public class SiteEditSettings {
	private String contentmanager;
	
	private List<Integer> publications;
	
	private boolean enabled;
	
    public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public String getContentmanager() {
		return contentmanager;
	}

	public void setContentmanager(String contentmanager) {
		this.contentmanager = contentmanager;
	}

	public List<Integer> getPublications() {
		return publications;
	}

	public void setPublications(List<Integer> publications) {
		this.publications = publications;
	}

    /**
     * Function indicates if a given publication has SiteEdit enabled.
     * 
     * @param pubid The Tridion Publication ID
     * @return true or false
     */
    public boolean hasPubSE(int pubid) {
    	if(publications == null) {
    		return enabled;
    	}
    	
        return publications.contains(pubid);
    }
}
