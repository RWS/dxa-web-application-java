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
 * Created on 12 Aug 2009
 * Created by Mihai Cadariu, SDL Tridion Sr Technical Consultant
 *
 * Last change in SVN: $Id: QueryStringUtil.java 6 2011-09-09 12:48:25Z rogier.oudshoorn $
 *
 * SDL Tridion, Communication That Connects
 */
package org.dd4t.core.util;

import java.net.MalformedURLException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class containing utility methods for handling query strings.
 * 
 * @author Mihai Cadariu, SDL Tridion Sr Technical Consultant
 */
public class QueryStringUtil {

	private static Logger logger = LoggerFactory.getLogger(QueryStringUtil.class);

	/**
	 * Unique sequence used internally in URL encoding
	 */
	public static final String SMART_SEQUENCE = "AmPeRsAnD";

	/**
	 * Private constructor. This is a static class. No instances are allowed.
	 */
	private QueryStringUtil() {
	}

	/**
	 * Return the path only of the given URL including the requested file name.
	 * 
	 * @param url
	 *            String representing the URL
	 * @return String the path and the file name of the given URL
	 */
	public static String getPath(String url) {
		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.getPath: Started for URL '" + url + "'");
		}

		if (url == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.getPath: Null URL passed, returning ''");
			}
			return "";
		} else {
			try {
				URL urlObj = new URL(url);
				url = urlObj.getPath();
			} catch (MalformedURLException murle) {
				int pos = url.indexOf("#");
				if (pos > 0) {
					url = url.substring(0, pos);
				}
				pos = url.indexOf("?");
				if (pos > 0) {
					url = url.substring(0, pos);
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.getPath: Returning '" + url + "'");
			}
			return url;
		}
	}

	/**
	 * Return the directory only of the given URL (without the filename). For /a/test.html, it returns /a. For
	 * /a/b/test.html, it return /a/b. For /test.html, it returns empty string. For /test, it returns empty string.
	 * 
	 * @param url
	 *            String representing the URL
	 * @return String the path and the file name of the given URL
	 */
	public static String getDirectory(String url) {
		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.getDirectory: Started for URL '" + url + "'");
		}

		url = getPath(url);
		int pos = url.lastIndexOf("/");
		if (pos > 0) {
			url = url.substring(0, pos);
		} else {
			url = "";
		}

		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.getDirectory: Returning '" + url + "'");
		}
		return url;
	}

	/*
	 * 
	 * /lola.html
	 * 
	 */

	/**
	 * For a given path, return the filename. That is the part between the last / and the end of the extension. If there
	 * is no filename, it returns empty String.
	 * 
	 * @param url
	 *            String representing the path to search get the filename from.
	 * @return String the filename or empty String
	 */
	public static String getFileName(String url) {
		String result = "";

		if (url == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.getFileName: Null URL passed, returning ''");
			}
			return result;
		} else {
			result = url;
			int pos = result.lastIndexOf("/");
			if (pos >= 0) {
				result = result.substring(pos + 1);
			}
			if (result.indexOf(".") < 0) {
				result = "";
			}
			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.getFileName: Returning '" + result + "'");
			}
			return result;
		}
	}

	/**
	 * Return the query part of the given URL
	 * 
	 * @param url
	 *            String representing the URL
	 * @return String representing the query of the URL
	 */
	public static String getQueryString(String url) {
		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.getQueryString: Started for URL '" + url + "'");
		}

		if (url == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.getQueryString: Null URL passed, returning ''");
			}
			return "";
		} else {
			try {
				URL urlObj = new URL(url);
				url = urlObj.getQuery();
			} catch (MalformedURLException murle) {
				int pos = url.indexOf("?");
				if (pos >= 0) {
					url = url.substring(pos + 1);
					pos = url.indexOf("#");
					if (pos >= 0) {
						url = url.substring(0, pos);
					}
				} else {
					url = "";
				}
			}

			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.getQueryString: Returning '" + url + "'");
			}
			return url;
		}
	}

	/**
	 * Sets the given query string to the given URL.
	 * 
	 * @param url
	 *            String representing the URL to attach the query string to
	 * @param qs
	 *            String representing the query string to attach to the URL
	 * @return String representing the URL
	 */
	public static String setQueryString(String url, String qs) {
		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.setQueryString: Started for URL '" + url + "', and query string '" + qs + "'");
		}

		if (url == null || qs == null) {
			if (logger.isDebugEnabled()) {
				logger.debug("QueryStringUtils.setQueryString: Null URL passed, returning ''");
			}
			return "";
		}

		url = getPath(url) + (qs.length() == 0 ? "" : "?" + qs);

		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.setQueryString: Returning '" + url + "'");
		}
		return url;
	}

	/**
	 * Return the query string as an array of name=value items
	 * 
	 * @param url
	 *            String representing the URL
	 * @return String[] representing the query string as name=value itemas
	 */
	/*public static String[] getQueryStringArray(String url) {
		logger.debug("QueryStringUtils.getQueryStringArray: Started for URL '" + url + "'");

		if (url == null) {
			logger.debug("QueryStringUtils.getQueryStringArray: Null URL passed, returning empty array []");
			return new String[0];
		}

		String[] qsArray = getQueryString(url).split("&");

		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.getQueryStringArray: Returning '" + Utils.arrayToString(qsArray, ",") + "'");
		}
		return qsArray;
	}*?

	/**
	 * Constructs a query string from an array of name=value items
	 * 
	 * @param qsArray
	 *            String[] representing the name=value items
	 * @return String representing the constructed query string
	 */
	/*public static String makeQueryString(String[] qsArray) {
		if (logger.isDebugEnabled()) {
			logger.debug("QueryStringUtils.makeQueryString: Started for query string array '" +
					Utils.arrayToString(qsArray, ",") + "'");
		}

		if (qsArray == null || qsArray.length == 0) {
			logger.debug("QueryStringUtils.makeQueryString: Null qsArray passed, returning ''");
			return "";
		}

		String qs = qsArray[0];
		for (int i = 1; i < qsArray.length; i++) {
			qs += "&" + qsArray[i];
		}

		logger.debug("QueryStringUtils.makeQueryString: Returning '" + qs + "'");
		return qs;
	}*/

	/**
	 * URL encodes all values in the query string of the given URL.<br>
	 * Example: /abc/page.jsp?n1=v1&n2=v2<br>
	 * It will URL encode values v1 and v2 and return the entire URL.
	 * 
	 * @param url
	 *            String representing the URL to encode the query string values for
	 * @return String representing the URL with all the value in the query string URL encoded
	 */
	/*public static String encodeQueryString(String url) {
		logger.debug("QueryStringUtil.encodeQueryString: Started for URL '" + url + "'");

		if (url == null) {
			logger.debug("QueryStringUtils.encodeQueryString: Null URL passed, returning ''");
			return "";
		}

		String[] qsArray = getQueryStringArray(url);
		for (int i = 0; i < qsArray.length; i++) {
			String nvPair = qsArray[i];
			String[] nvArray = nvPair.split("=", 2);
			if (nvArray.length > 1) {
				try {
					qsArray[i] = nvArray[0] + "=" + URLEncoder.encode(nvArray[1], "UTF-8");
				} catch (UnsupportedEncodingException uee) {
					logger.error("QueryStringUtils.encodeQueryString: Exception occurred " + uee, uee);
				}
			}
		}

		url = setQueryString(url, makeQueryString(qsArray));

		logger.debug("QueryStringUtils.encodeQueryString: Returning '" + url + "'");
		return url;
	}*/

	/**
	 * Encode the ampersands ( & ) in the given URL.
	 * 
	 * @param url
	 *            String representing the URL to encode ampersands in
	 * @return String containing the given URL with ampersands URL encoded
	 * @see QueryStringUtil#urlDecodeAmpersand(String)
	 */
	public static String urlEncodeAmpersand(String url) {
		url = url.replaceAll("&amp;", "&");
		url = url.replaceAll("&", SMART_SEQUENCE);

		return url;
	}

	/**
	 * Decode the ampersands ( & ) in the given URL. The ampersands must have been previously encoded using
	 * urlEncodeAmpersands.
	 * 
	 * @param url
	 *            String representing the URL to encode ampersands in
	 * @return String containing the given URL with ampersands URL encoded
	 * @see QueryStringUtil#urlEncodeAmpersand(String)
	 */
	public static String urlDecodeAmpersand(String url) {
		url = url.replaceAll("SMART_SEQUENCE", "%26");

		return url;
	}
}
