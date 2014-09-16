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
 * Tridion Delivery Framework
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
 * Created on Nov 26, 2004
 * Create by Maurice Rootjes, Tridion Principal Consultant
 * Maintained by Mihai Cadariu, Senior Technical Consultant
 * 
 * Last change in SVN: $Id: BrokerUtils.java 6 2011-09-09 12:48:25Z rogier.oudshoorn $
 *
 * SDL Tridion, Communication That Connects
 */
package org.dd4t.core.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tridion.ItemTypes;
import com.tridion.broker.StorageException;
import com.tridion.broker.binaries.meta.BinaryMetaHome;
import com.tridion.broker.components.meta.ComponentMetaHome;
import com.tridion.broker.pages.meta.PageMetaHome;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.PageMeta;
import com.tridion.util.TCDURI;
import com.tridion.util.TCMURI;

public class BrokerUtils {

	private static Logger logger = LoggerFactory.getLogger(BrokerUtils.class);

	private static final String SQL_GETROLES = "select distinct KEYWORD from ITEM_CATEGORIES_AND_KEYWORDS where CATEGORY = ?";

	private static List<String> roles = new ArrayList<String>();

	private static boolean urlNormalisation = false;
	
	/**
	 * This class will only be used for static helper functions
	 */
	private BrokerUtils() {
		super();
	}

	/*
	public static String[] getAvailableRoles() throws StorageException {
		if (roles.isEmpty()) {
			Connection conn = null;
			ResultSet rset = null;
			PreparedStatement pstmtGetRoles = null;
			try {
				// try to read the data from the database
				conn = SQLConnectionManager.getInstance().getConnection();
				if (conn != null) {
					pstmtGetRoles = conn.prepareStatement(SQL_GETROLES);
					pstmtGetRoles.setString(1, "roles");
					rset = pstmtGetRoles.executeQuery();

					while (rset.next()) {
						roles.add(rset.getString(1));
					}
				} else {
					throw new StorageException("Could not get connection");
				}
			} catch (SQLException se) {
				throw new StorageException("Error retrieving roles", se);
			} finally { // try to clean up after ourselves
				if (rset != null) {
					try {
						rset.close();
					} catch (SQLException se1) {
						throw new StorageException("Error retrieving roles", se1);
					}
				}
				if (pstmtGetRoles != null) {
					try {
						pstmtGetRoles.close();
					} catch (SQLException se2) {
						throw new StorageException("Error retrieving roles", se2);
					}
				}
				if (conn != null) {
					try {
						conn.close();
					} catch (SQLException se3) {
						throw new StorageException("Error retrieving roles", se3);
					} finally {
						// whatever else happens, always release the connection.
						SQLConnectionManager.getInstance().releaseConnection(conn);
					}
				}
			}
		}
		return (String[]) roles.toArray(new String[0]);
	}*/

	/**
	 * Parse the itemURI given as parameter to a TCMURI object and return the Publication ID part of it.
	 * 
	 * @param itemURI
	 *            String representing the TCMURI to process
	 * @return int the item ID of the TCMURI, or 0 if itemURI cannot be parsed to TCMURI
	 */
	public static int getPublicationId(String itemURI) {
		try {
			TCMURI uri = new TCMURI(itemURI);
			return uri.getPublicationId();
		} catch (ParseException pe) {
			logger.error("BrokerUtils.getPublicationId: Cannot parse itemURI '" + itemURI + "' as TCMURI object", pe);
		}

		return 0;
	}

	/**
	 * Parse the itemURI given as parameter to a TCMURI object and return the item ID part of it.
	 * 
	 * @param itemURI
	 *            String representing the TCMURI to process
	 * @return int the item ID of the TCMURI, or 0 if itemURI cannot be parsed to TCMURI
	 */
	public static int getItemId(String itemURI) {
		try {
			TCMURI uri = new TCMURI(itemURI);
			return uri.getItemId();
		} catch (ParseException pe) {
			logger.error("BrokerUtils.getItemId: Cannot parse itemURI '" + itemURI + "' as TCMURI object", pe);
		}

		return 0;
	}

	/**
	 * Decodes the given URI into a Broker URL. The following characters are not % encoded by the publisher (R5.3SP1 +
	 * hotfix CM_5.3.1.62187): {.}, {'}
	 * 
	 * @param input
	 *            a String representing the URI to decode
	 * @return the decoded String
	 */
	public static String decodeURIForBrokerLookup(String input) {
		String result = input;

		// TODO: is it necessary to have this configurable? TDFConfiguration.getURLNormalisation()
		if (urlNormalisation) {
			result = decodeUrl(result);
		} else {
			result = result.replaceAll("%2E", ".");
			result = result.replaceAll("%27", "'");
		}

		return result;
	}

	/**
	 * Decode the url changing it into valid utf8 encoding. Used to change %20 in urls into spaces.
	 * 
	 * @param input
	 *            The url that must be decoded
	 * @return The decoded url or the original url if an exception occurs durin decoding.
	 */
	public static String decodeUrl(String input) {
		try {
			return URLDecoder.decode(input, "UTF-8");
		} catch (UnsupportedEncodingException uee) {
			logger.error("BrokerUtils.decodeUrl: Cannot decode URL '" + input + "'", uee);
			return input;
		}
	}

	/**
	 * Get a PageMeta object by looking it up in the Broker.<br>
	 * 
	 * @param publicationId
	 *            int representing the Publication context to do the lookup in
	 * @param url
	 *            String representing the URL of the Page to lookup
	 * @return PageMeta the found PageMeta object, or null otherwise
	 * 
	 * @author Mihai Cadariu
	 * @since 2009-03-06
	 */
	public static PageMeta getPageMetaByURL(int publicationId, String url) {
		if (logger.isDebugEnabled()) {
			logger.debug("BrokerUtils.getPageMetaByURL: Started with PublicationId " + publicationId + " and URL '" + url +
				"'");
		}
		
		PageMetaHome pageMetaHome = HomeUtils.getInstance().getPageMetaHome();

		try {
			PageMeta pageMeta = pageMetaHome.findByURL(publicationId, url);
			if (pageMeta == null) {
				logger.warn("BrokerUtils.getPageMetaByURL: Cannot find PageMeta in Tridion. Returning null.");
				return null;
			}
			if (logger.isDebugEnabled()) {
				logger.debug("BrokerUtils.getPageMetaByURL: Returning PageMeta " + pageMeta);
			}
			return pageMeta;
		} catch (StorageException se) {
			logger.error("BrokerUtils.getPageMetaByURL: StorageException caught, returning null.");
			return null;
		}
	}

	/**
	 * Get a PageMeta object by looking it up in the Broker.<br>
	 * 
	 * @param publicationId
	 *            int representing the Publication context to do the lookup in
	 * @param pageId
	 *            int representing the id of the Page to lookup
	 * @return PageMeta the found PageMeta object, or null otherwise
	 * 
	 * @author Mihai Cadariu
	 * @since 2009-03-07
	 */
	public static PageMeta getPageMeta(int publicationId, int pageId) {
		if (logger.isDebugEnabled()) {
			logger.debug("BrokerUtils.getPageMeta: Started with PublicationId " + publicationId + " and PageId " + pageId);
		}

		PageMetaHome pageMetaHome = HomeUtils.getInstance().getPageMetaHome();

		try {
			PageMeta pageMeta = pageMetaHome.findByPrimaryKey(publicationId, pageId);
			if (pageMeta == null) {
				logger.warn("BrokerUtils.getPageMeta: Cannot find PageMeta in Tridion. Returning null.");
				return null;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("BrokerUtils.getPageMeta: Returning PageMeta " + pageMeta);
			}
			return pageMeta;
		} catch (StorageException se) {
			logger.error("BrokerUtils.getPageMeta: StorageException caught, returning null.");
			return null;
		}
	}

	/**
	 * Get a ComponentMeta object by looking it up in the Broker.<br>
	 * 
	 * @param publicationId
	 *            int representing the Publication context to do the lookup in
	 * @param itemId
	 *            int representing the id of the Component to lookup
	 * @return ComponentMeta the found ComponentMeta object, or null otherwise
	 * 
	 * @author Mihai Cadariu
	 * @since 2009-03-07
	 */
	public static ComponentMeta getComponentMeta(int publicationId, int itemId) {
		if (logger.isDebugEnabled()) {
			logger.debug("BrokerUtils.getComponentMeta: Started with PublicationId " + publicationId + " and itemId " +
					itemId);
		}
				

		ComponentMetaHome componentMetaHome = HomeUtils.getInstance().getComponentMetaHome();

		try {
			ComponentMeta componentMeta = componentMetaHome.findByPrimaryKey(publicationId, itemId);
			if (componentMeta == null) {
				logger.warn("BrokerUtils.getComponentMeta: Cannot find ComponentMeta in Tridion. Returning null.");
				return null;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("BrokerUtils.getComponentMeta: Returning ComponentMeta " + componentMeta);
			}
			return componentMeta;
		} catch (StorageException se) {
			logger.error("BrokerUtils.getComponentMeta: StorageException caught, returning null.");
			return null;
		}
	}

	/**
	 * Get a BinaryMeta object by looking it up in the Broker.<br>
	 * 
	 * @param publicationId
	 *            int representing the Publication context to do the lookup in
	 * @param itemId
	 *            int representing the id of the Binary to lookup
	 * @return BinaryMeta the found BinaryMeta object, or null otherwise
	 * 
	 * @author Mihai Cadariu
	 * @since 2009-07-21
	 */
	public static BinaryMeta getBinaryMeta(int publicationId, int itemId) {
		logger
				.debug("BrokerUtils.getBinaryMeta: Started with PublicationId " + publicationId + " and itemId " +
						itemId);

		BinaryMetaHome binaryMetaHome = HomeUtils.getInstance().getBinaryMetaHome();

		try {
			TCDURI tcduri = new TCDURI(publicationId, itemId, ItemTypes.COMPONENT);
			BinaryMeta binaryMeta = binaryMetaHome.findByPrimaryKey(tcduri);
			if (binaryMeta == null) {
				logger.warn("BrokerUtils.getBinaryMeta: Cannot find BinaryMeta in Tridion. Returning null.");
				return null;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("BrokerUtils.getBinaryMeta: Returning BinaryMeta " + binaryMeta);
			}
			return binaryMeta;
		} catch (StorageException se) {
			logger.error("BrokerUtils.getBinaryMeta: StorageException caught, returning null.");
			return null;
		}
	}

	/**
	 * Get a BinaryMeta object by looking it up in the Broker.<br>
	 * 
	 * @param publicationId
	 *            int representing the Publication context to do the lookup in
	 * @param url
	 *            String representing the URL of the Page to lookup
	 * @return PageMeta the found PageMeta object, or null otherwise
	 * 
	 * @author Mihai Cadariu
	 * @since 2009-03-09
	 */
	public static BinaryMeta getBinaryMetaByURL(int publicationId, String url) {
		if (logger.isDebugEnabled()) {
			logger.debug("BrokerUtils.getBinaryMetaByURL: Started with PublicationId " + publicationId + " and URL '" +
				url + "'");
		}
		
		BinaryMetaHome binaryMetaHome = HomeUtils.getInstance().getBinaryMetaHome();

		try {
			BinaryMeta binaryMeta = binaryMetaHome.findByURL(publicationId, url);
			if (binaryMeta == null) {
				logger.warn("BrokerUtils.getBinaryMetaByURL: Cannot find BinaryMeta in Tridion. Returning null.");
				return null;
			}

			if (logger.isDebugEnabled()) {
				logger.debug("BrokerUtils.getBinaryMetaByURL: Returning BinaryMeta " + binaryMeta);
			}
			return binaryMeta;
		} catch (StorageException se) {
			logger.error("BrokerUtils.getBinaryMetaByURL: StorageException caught, returning null");
			return null;
		}
	}
}
