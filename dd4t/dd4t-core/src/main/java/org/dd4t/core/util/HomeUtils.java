/**  
 *  Copyright 2011 Capgemini & SDL
 * 
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
/**
 * SDL Tridion, Tridion Delivery Framework
 * 
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL TRIDION BE LIABLE FOR ANY DIRECT, 
 * INDIRECT, INCIDENTAL,SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) 
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, 
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING 
 * IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE 
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * Created on 14 March 2008 by Mihai Cadariu, Tridion Technical Consultant
 * 
 * Last change in SVN: $Id: HomeUtils.java 6 2011-09-09 12:48:25Z rogier.oudshoorn $
 *
 * SDL Tridion, Communication That Connects
 */
package org.dd4t.core.util;

import java.text.ParseException;
import java.util.HashMap;

import javax.naming.Context;
import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.broker.StorageException;
import com.tridion.broker.binaries.BinaryHome;
import com.tridion.broker.binaries.meta.BinaryMetaHome;
import com.tridion.broker.componentpresentations.ComponentPresentationHome;
import com.tridion.broker.componentpresentations.meta.ComponentPresentationMetaHome;
import com.tridion.broker.components.meta.ComponentMetaHome;
import com.tridion.broker.linking.DynamicLinkInfoHome;
import com.tridion.broker.linking.LinkInfoHome;
import com.tridion.broker.pages.FSPageHome;
import com.tridion.broker.pages.PageHome;
import com.tridion.broker.pages.SQLPageHome;
import com.tridion.broker.pages.meta.PageMetaHome;
import com.tridion.dcp.ComponentPresentationFactory;
import com.tridion.naming.CDInitialContextFactory;
import com.tridion.storage.StorageManagerFactory;
import com.tridion.storage.StorageTypeMapping;
import com.tridion.storage.dao.BinaryVariantDAO;
import com.tridion.storage.dao.ItemDAO;
import com.tridion.util.TCMURI;

/**
 * This class provides convenience methods for home classes.
 * 
 * @author Mihai Cadariu
 */
public class HomeUtils {

	private static Logger logger = LoggerFactory.getLogger(HomeUtils.class);

	private HashMap<Integer, ComponentPresentationFactory> cpFactoryStore = new HashMap<Integer, ComponentPresentationFactory>();
	
	private PageHome pageHome = null;

	private PageMetaHome pageMetaHome = null;

	private BinaryHome binaryHome = null;

	private BinaryMetaHome binaryMetaHome = null;

	private ComponentMetaHome componentMetaHome = null;

	private ComponentPresentationHome componentPresentationHome = null;

	private ComponentPresentationMetaHome componentPresentationMetaHome = null;

	private DynamicLinkInfoHome dynamicLinkInfoHome = null;

	private LinkInfoHome linkInfoHome = null;

	private FSPageHome pageHomeFS = null;

	private SQLPageHome pageHomeSQL = null;

	private static HomeUtils instance = new HomeUtils();
	
	private HashMap<Integer, ItemDAO> binaryDAOs = new HashMap<Integer, ItemDAO>();
	private HashMap<Integer, BinaryVariantDAO> binaryVariantDAOs = new HashMap<Integer, BinaryVariantDAO>();
	private HashMap<Integer, ItemDAO> binaryMetaDAOs = new HashMap<Integer, ItemDAO>();

	
	/**
	 * Private Constructor. Singleton implementation.
	 */
	private HomeUtils() {
	}

	/**
	 * Gets an instance of this HomeUtils class
	 */
	public static HomeUtils getInstance() {
		return instance;
	}

	/**
	 * Get the Page home class
	 * 
	 * @return Page home class, or null if not found
	 */
	public PageHome getPageHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getPageHome: Getting the Page home class");
		}

		if (pageHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				pageHome = (PageHome) context.lookup("java://comp/env/data/Page");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getPageHome: Retrieved Page home instance: " + pageHome);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (pageHome == null) {
				logger.warn("HomeUtils.getPageHome: Page home class not found");
			}
		}

		return pageHome;
	}

	/**
	 * Get the PageMeta home class
	 * 
	 * @return PageMeta home class, or null if not found
	 * @deprecated
	 */
	public PageMetaHome getPageMetaHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getPageMetaHome: Getting the PageMeta home class");
		}

		if (pageMetaHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				pageMetaHome = (PageMetaHome) context.lookup("java://comp/env/data/PageMeta");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getPageMetaHome: Retrieved PageMeta instance: " + pageMetaHome);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (pageMetaHome == null) {
				logger.warn("HomeUtils.getPageMetaHome: PageMeta home class not found");
			}
		}

		return pageMetaHome;
	}

	/**
	 * Get the Binary home class
	 * 
	 * @return Binary home class, or null if not found
	 */
	public BinaryHome getBinaryHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getBinaryHome: Getting the Binary home class");
		}

		if (binaryHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				binaryHome = (BinaryHome) context.lookup("java://comp/env/data/Binary");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getBinaryHome: Retrieved Binary instance: " + binaryHome);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (binaryHome == null) {
				logger.warn("HomeUtils.getBinaryHome: BinaryHome not found");
			}
		}

		return binaryHome;
	}

	public ItemDAO getBinaryMetaDao(String publicationUri) throws ParseException, StorageException {
		TCMURI tcmUri = new TCMURI(publicationUri);
		return getBinaryMetaDao(tcmUri.getItemId());
	}

	public BinaryVariantDAO getBinaryVariantDao(int publicationId) throws StorageException {
		Integer pub = new Integer(publicationId);
		if (! binaryVariantDAOs.containsKey(pub)) {
			binaryVariantDAOs.put(pub, (BinaryVariantDAO) StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.BINARY_VARIANT));
		}
		return binaryVariantDAOs.get(pub);		
	}

	public ItemDAO getBinaryDao(int publicationId) throws StorageException {
		Integer pub = new Integer(publicationId);
		if (! binaryDAOs.containsKey(pub)) {
			binaryDAOs.put(pub, (ItemDAO) StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.BINARY_CONTENT));
		}
		return binaryDAOs.get(pub);
	}
	public ItemDAO getBinaryMetaDao(int publicationId) throws StorageException {
		Integer pub = new Integer(publicationId);
		if (! binaryMetaDAOs.containsKey(pub)) {
			binaryMetaDAOs.put(pub, (ItemDAO) StorageManagerFactory.getDAO(publicationId, StorageTypeMapping.BINARY_CONTENT));
		}
		return binaryMetaDAOs.get(pub);
	}
	
	/**
	 * Get the BinaryMeta home class
	 * 
	 * @return BinaryMeta home class, or null if not found
	 */
	public BinaryMetaHome getBinaryMetaHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getBinaryMetaHome: Getting the BinaryMetaHome");
		}

		if (binaryMetaHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				binaryMetaHome = (BinaryMetaHome) context.lookup("java://comp/env/data/BinaryMeta");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getBinaryMetaHome: Retrieved BinaryMeta instance: " + binaryMetaHome);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (binaryMetaHome == null) {
				logger.warn("HomeUtils.getBinaryMetaHome: BinaryMetaHome not found");
			}
		}

		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getBinaryMetaHome: returning " + binaryMetaHome);
		}
		return binaryMetaHome;
	}

	/**
	 * Get the ComponentMeta home class
	 * 
	 * @return ComponentMeta home class, or null if not found
	 */
	public ComponentMetaHome getComponentMetaHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getComponentMetaHome: Getting the ComponentMeta home class");
		}

		if (componentMetaHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				componentMetaHome = (ComponentMetaHome) context.lookup("java://comp/env/data/ComponentMeta");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getComponentMetaHome: Retrieved ComponentMeta instance: " + componentMetaHome);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (componentMetaHome == null) {
				logger.warn("HomeUtils.getComponentMetaHome: ComponentMetaHome not found");
			}
		}

		return componentMetaHome;
	}

	/**
	 * Get the ComponentPresentationMeta home class
	 * 
	 * @return ComponentPresentationMeta home class, or null if not found
	 */
	public ComponentPresentationMetaHome getComponentPresentationMetaHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getComponentPresentationMetaHome: Getting the ComponentPresentationMeta home class");
		}

		if (componentPresentationMetaHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				componentPresentationMetaHome = (ComponentPresentationMetaHome) context
						.lookup("java://comp/env/data/ComponentPresentationMeta");
				logger
						.debug("HomeUtils.getComponentPresentationMetaHome: Retrieved ComponentPresentationMeta instance: " +
								componentPresentationMetaHome);
			} catch (NamingException ne) {
				logger.error("HomeUtils.getComponentPresentationMetaHome: " + ne.getMessage(), ne);
			}

			if (componentPresentationMetaHome == null) {
				logger.warn("HomeUtils.getComponentPresentationMetaHome: ComponentPresentationMetaHome not found");
			}
		}

		return componentPresentationMetaHome;
	}

	/**
	 * Get the ComponentPresentation home class
	 * 
	 * @return ComponentPresentation home class, or null if not found
	 */
	public ComponentPresentationHome getComponentPresentationHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getComponentPresentationHome: Getting the ComponentPresentation home class");
		}

		if (componentPresentationHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				componentPresentationHome = (ComponentPresentationHome) context
						.lookup("java://comp/env/data/ComponentPresentation");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getComponentPresentationHome: Retrieved ComponentPresentation instance: " +
						componentPresentationHome);
				}
			} catch (NamingException ne) {
				logger.error("HomeUtils.getComponentPresentationHome: " + ne.getMessage(), ne);
			}

			if (componentPresentationHome == null) {
				logger.warn("HomeUtils.getComponentPresentationHome: ComponentPresentationHome not found");
			}
		}

		return componentPresentationHome;
	}

	/**
	 * Get the DynamicLinkInfo home class
	 * 
	 * @return DynamicLinkInfo home class, or null if not found
	 */
	public DynamicLinkInfoHome getDynamicLinkInfoHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getDynamicLinkInfoHome: Getting the DynamicLinkInfo home class");
		}

		if (dynamicLinkInfoHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				dynamicLinkInfoHome = (DynamicLinkInfoHome) context.lookup("java://comp/env/data/DynamicLinkInfo");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getDynamicLinkInfoHome: Retrieved DynamicLinkInfo instance: " +
						dynamicLinkInfoHome);
				}
			} catch (NamingException ne) {
				logger.error("HomeUtils.getDynamicLinkInfoHome: " + ne.getMessage(), ne);
			}

			if (dynamicLinkInfoHome == null) {
				logger.warn("HomeUtils.getDynamicLinkInfoHome: DynamicLinkInfoHome not found");
			}
		}

		return dynamicLinkInfoHome;
	}

	/**
	 * Get the LinkInfo home class
	 * 
	 * @return LinkInfo home class, or null if not found
	 */
	public LinkInfoHome getLinkInfoHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getLinkInfoHome: Getting the LinkInfo home class");
		}

		if (linkInfoHome == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				linkInfoHome = (LinkInfoHome) context.lookup("java://comp/env/data/LinkInfo");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getLinkInfoHome: Retrieved LinkInfo instance: " + linkInfoHome);
				}
			} catch (NamingException ne) {
				logger.error("HomeUtils.getLinkInfoHome: " + ne.getMessage(), ne);
			}

			if (linkInfoHome == null) {
				logger.warn("HomeUtils.getLinkInfoHome: LinkInfoHome not found");
			}
		}

		return linkInfoHome;
	}

	/**
	 * Get the FileSystem Page home class
	 * 
	 * @return FSPage home class, or null if not found
	 */
	public FSPageHome getFSPageHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getFSPageHome: Getting the FS Page home class");
		}

		if (pageHomeFS == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				pageHomeFS = (FSPageHome) context.lookup("java://comp/env/data/FSPage");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getFSPageHome: Retrieved FS Page home instance: " + pageHomeFS);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (pageHomeFS == null) {
				logger.warn("HomeUtils.getFSPageHome: FS Page home class not found");
			}
		}

		return pageHomeFS;
	}

	/**
	 * Get the SQL Page home class
	 * 
	 * @return SQLPageHome class, or null if not found
	 */
	public SQLPageHome getSQLPageHome() {
		if (logger.isDebugEnabled()) {
			logger.debug("HomeUtils.getSQLPageHome: Getting the SQL Page home class");
		}

		if (pageHomeSQL == null) {
			try {
				Context context = (new CDInitialContextFactory()).getInitialContext(null);
				pageHomeSQL = (SQLPageHome) context.lookup("java://comp/env/data/SQLPage");
				if (logger.isDebugEnabled()) {
					logger.debug("HomeUtils.getSQLPageHome: Retrieved SQL Page home instance: " + pageHomeSQL);
				}
			} catch (NamingException e) {
				logger.error(e.getMessage(), e);
			}

			if (pageHomeSQL == null) {
				logger.warn("HomeUtils.getSQLPageHome: SQL Page home class not found");
			}
		}

		return pageHomeSQL;
	}
	
	/**
	 * Get the ComponentPresentationFactory for the publication
	 * 
	 * @param publicationId
	 * @return
	 */
	public ComponentPresentationFactory getComponentPresentationFactory(int publicationId) {
		Integer pubInt = Integer.valueOf(publicationId);
		if (! cpFactoryStore.containsKey(pubInt)) {
			ComponentPresentationFactory cpFactory = new ComponentPresentationFactory(publicationId);
			cpFactoryStore.put(pubInt, cpFactory);
		}
		return cpFactoryStore.get(pubInt);
	}

}
