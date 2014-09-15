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
package org.dd4t.core.factories.impl;

import java.io.IOException;
import java.text.ParseException;

import org.dd4t.contentmodel.GenericPage;
import org.dd4t.contentmodel.exceptions.ItemNotFoundException;
import org.dd4t.contentmodel.exceptions.NotAuthenticatedException;
import org.dd4t.contentmodel.exceptions.NotAuthorizedException;
import org.dd4t.contentmodel.impl.BasePublishedItem;
import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.filters.FilterException;
import org.dd4t.core.filters.impl.BaseFilter;
import org.dd4t.core.request.RequestContext;
import org.dd4t.core.util.TridionUtils;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StopWatch;

import com.tridion.broker.StorageException;
import com.tridion.storage.PageMeta;
import com.tridion.util.TCMURI;

public class GenericPageFactory extends BaseFactory implements PageFactory {

	private static Logger logger = LoggerFactory.getLogger(GenericPageFactory.class);
		
	private PageProvider pageProvider;
	
	public PageProvider getPageProvider() {
		if(pageProvider == null){
			pageProvider = new BrokerPageProvider();
		}
		return pageProvider;
	}

	public void setPageProvider(PageProvider provider) {
		this.pageProvider = provider;
	}
	
	
	/**
	 * Get a page by its URI. No security available; the method will fail if a
	 * SecurityFilter is configured on the factory.
	 * 
	 * @param uri
	 *            of the page
	 * @return
	 * @throws ItemNotFoundException
	 * @throws  
	 */
	public GenericPage getPage(String uri) throws ItemNotFoundException {

		GenericPage page = null;
		try {
			page = this.getGenericPage(uri, null);
		} catch (NotAuthorizedException e) {
			// can be ignored
			logger.warn("unexpected NotAuthorizedException: no SecurityFilter is configured");
		} catch(NotAuthenticatedException e2){
			
		}
		return page;
	}

	/**
	 * Get a page by the uri.
	 * 
	 * @return the page
	 * @throws NotAuthorizedException
	 *             if not allowed to get the page.
	 * @throws ItemNotFoundException
	 *             if no page found.
	 * @throws NotAuthenticatedException 
	 */
	@Override
	public GenericPage getPage(String uri, RequestContext context)
			throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {

		return this.getGenericPage(uri, context);
	}

	private GenericPage getGenericPage(String uri, RequestContext context)
			throws ItemNotFoundException, NotAuthorizedException, NotAuthenticatedException {

		if (context == null && securityFilterPresent()) {
			throw new RuntimeException(
					"use of getPage is not allowed when a SecurityFilter is set");
		}
		
		StopWatch stopWatch = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Enter getGenericPage with uri: " + uri);
			stopWatch = new StopWatch("getGenericPage");
			stopWatch.start();
		}

		GenericPage page = (GenericPage) getCacheProvider()
				.loadFromLocalCache(uri);
		if (page == null) {
			try {
				TCMURI tcmUri = new TCMURI(uri);
				PageMeta pageMeta = getPageProvider().getPageMetaById(tcmUri.getItemId(), tcmUri.getPublicationId());

				if(pageMeta == null)
					throw new ItemNotFoundException("Unable to find page by id "+tcmUri);
					
				page = getPageFromMeta(pageMeta);

				try {
					// run only the filters where the result is allowed to be
					// cached.
					doFilters(page, context, BaseFilter.RunPhase.BeforeCaching);
				} catch (FilterException e) {
					logger.error("Error in filter. ", e);
					throw new RuntimeException(e);
				}
				
				getCacheProvider().storeInItemCache(uri, page, tcmUri.getPublicationId(), tcmUri.getItemId());

			} catch(IOException e){
				logger.error(
						"IOException when processing page: " + uri, e);
				throw new RuntimeException(e);
			} catch (ParseException e) {			
				logger.error("ParseException when searching for page: " + uri,
						e);
				throw new RuntimeException(e);
			} catch (StorageException e) {
				logger.error(
						"StorageException when searching for page: " + uri, e);
				throw new RuntimeException(e);
			}
		}

		try {
			// run only the filters where the result is not allowed to be
			// cached.
			doFilters(page, context, BaseFilter.RunPhase.AfterCaching);
		} catch (FilterException e) {
			logger.error("Error in filter. ", e);
			throw new RuntimeException(e);
		} finally {
			if (logger.isDebugEnabled()) {
				stopWatch.stop();
				logger.debug("Exit getGenericPage ("
						+ stopWatch.getTotalTimeMillis() + " ms)");
			}
		}

		return page;
	}

	/**
	 * Find page by its URL. The url and publication id are specified. No
	 * security available; the method will fail if a SecurityFilter is
	 * configured on the factory.
	 * 
	 * @return
	 * @throws ItemNotFoundException
	 */
	public GenericPage findPageByUrl(String url, int publicationId)
			throws ItemNotFoundException {

		GenericPage page = null;
		try {
			page = this.findGenericPageByUrl(url, publicationId, null);
		} catch (NotAuthorizedException e) {
			// can be ignored
			logger.warn("unexpected NotAuthorizedException: no SecurityFilter is configured");
		}catch(NotAuthenticatedException e2){
			
		}

		return page;
	}

	/**
	 * Get a page by the url and publication id.
	 * 
	 * @return the page
	 * @throws NotAuthorizedException
	 *             if not allowed to get the page.
	 * @throws ItemNotFoundException
	 *             if no page found.
	 * @throws NotAuthenticatedException 
	 */
	@Override
	public GenericPage findPageByUrl(String url, int publicationId,
			RequestContext context) throws ItemNotFoundException,
			NotAuthorizedException, NotAuthenticatedException {
		return this.findGenericPageByUrl(url, publicationId, context);
	}

	/**
	 * This is just a intermediate method to avoid naming conflicts with the
	 * simpleFactory.
	 * @throws NotAuthenticatedException 
	 */
	private GenericPage findGenericPageByUrl(String url, int publicationId,
			RequestContext context) throws ItemNotFoundException,
			NotAuthorizedException, NotAuthenticatedException {

		if (context == null && securityFilterPresent()) {
			throw new RuntimeException(
					"use of findPageByUrl is not allowed when a SecurityFilter is set");
		}

		StopWatch stopWatch = null;
		StopWatch subWatch = null;
		if (logger.isDebugEnabled()) {
			logger.debug("Enter findGenericPageByUrl with url:" + url
					+ " and publicationId: " + publicationId);
			stopWatch = new StopWatch("findGenericPageByUrl");
			stopWatch.start();
			
			subWatch = new StopWatch("subTasks");
			subWatch.start();
		}

		String cacheKey = publicationId + "-" + url;
		GenericPage page = (GenericPage) getCacheProvider().loadFromLocalCache(
				cacheKey);
		if (page == null) {
			try {
				PageMeta pageMeta = getPageProvider().getPageMetaByURL(url, publicationId);

				page = getPageFromMeta(pageMeta);

				try {
					// run only the filters where the result is allowed to be
					// cached.
					doFilters(page, context, BaseFilter.RunPhase.BeforeCaching);
				} catch (FilterException e) {
					logger.warn("Error in filter. ", e);
					throw new RuntimeException(e);
				}

				getCacheProvider().storeInItemCache(cacheKey, page, pageMeta.getPublicationId(), pageMeta.getItemId());

			} catch(IOException e){
				logger.error(
						"IOException when processing page: " + url, e);
				throw new RuntimeException(e);
			} catch (StorageException e) {
				logger.error(
						"StorageException when searching for page: " + url, e);
				throw new RuntimeException(e);
			}
		}
		
		if (logger.isDebugEnabled()) {
			subWatch.stop();
			logger.debug("Page retrieved in "
					+ subWatch.getTotalTimeMillis() + " ms)");
			subWatch = new StopWatch("again");
			subWatch.start();
		}

		try {
			// run only the filters where the result is not allowed to be
			// cached.
			doFilters(page, context, BaseFilter.RunPhase.AfterCaching);
		} catch (FilterException e) {
			logger.error("Error in filter. ", e);
			throw new RuntimeException(e);
		} finally {
			if (logger.isDebugEnabled()) {
				subWatch.stop();
				logger.debug("Ran filters in "
						+ subWatch.getTotalTimeMillis() + " ms)");
				stopWatch.stop();
				logger.debug("Exit findGenericPageByUrl for "+url+" ("
						+ stopWatch.getTotalTimeMillis() + " ms)");
			}
		}

		return page;
	}
	
	/**
	 * Private function transforms a pageMeta into a GenericPage.
	 * 
	 * @param pageMeta
	 * @return
	 * @throws StorageException
	 * @throws IOException
	 * @throws ItemNotFoundException 
	 */
	private GenericPage getPageFromMeta(PageMeta pageMeta) throws StorageException, IOException, ItemNotFoundException{
		String source = getPageProvider().getPageXMLByMeta(pageMeta);
		
		if(source == null)
			throw new ItemNotFoundException("Unable find source for given page");
		
		GenericPage page = getPageFromSource(source);
		
		page.setNativeMetadata(pageMeta);				
		page.setId(TridionUtils.createUri(pageMeta).toString());
		page.setTitle(pageMeta.getTitle());
		
		// object size is roughly half the xml string size				
		((BasePublishedItem) page).setSourceSize(source.length());	
		
		return page;
	}

	/**
	 * Transform source into a page.
	 * 
	 * @param source
	 * @return
	 */
	public GenericPage getPageFromSource(String source) {

		StopWatch stopWatch = null;
		try {
			if (logger.isDebugEnabled()) {
				stopWatch = new StopWatch();
				stopWatch.start("getPageFromSource");
			}
			return (GenericPage) this.getSerializer().deserialize(source, GenericPage.class);	
		} catch (Exception e) {
			logger.error("Exception when deserializing page: ", e);
			throw new RuntimeException(e);
		} finally {
			if (logger.isDebugEnabled()) {
				stopWatch.stop();
				logger.debug("Deserialization of page took: "
						+ stopWatch.getLastTaskTimeMillis() + " ms");
			}
		}

	}
}