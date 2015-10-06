/*
 * Copyright (c) 2015 SDL, Radagio & R. Oudshoorn
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.dd4t.providers.rs;

import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.providers.QueryProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Configurable;

import javax.ws.rs.ClientErrorException;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.ProcessingException;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Collection;
import java.util.Map;

/**
 * Client side of the Component provider. This communicates with the service layer to read components.
 * The communication is done using JAX-RS 1.x singleton client.
 * <p/>
 * No Tridion dependencies are allowed here.
 *
 * @author R. Kempees
 */
@Configurable
public class BrokerQueryProvider extends BaseBrokerProvider implements QueryProvider {

	private static final Logger LOG = LoggerFactory.getLogger(BrokerQueryProvider.class);

	@Override
	public String[] getDynamicComponentPresentationsByCustomMetaQuery (final String locale, final Map<String, Collection<String>> keyValueMap, final int templateId) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching Component Presentation by locale: {}, keyValueMap: {}", new Object[]{locale, keyValueMap});

		try {
			// Get the Client Request object for this query: by encoding all the Query parameters properly

			String queryLocale = appendSlashIfRequired(locale);

			StringBuilder queryValue = new StringBuilder();
			for (Map.Entry<String,Collection<String>> entrySet : keyValueMap.entrySet()) {
				for (String value : entrySet.getValue()) {
					if (queryValue.length() > 0) {
						queryValue.append("&");
					}
					queryValue.append(entrySet.getKey().trim()).append("=").append(value.trim());
				}
			}
			String encodedParameter = encodeUrl(queryValue.toString()).trim();
			WebTarget request = client.getQueryComponentByCustomMetaRequest().path(encodeUrl(queryLocale.toLowerCase())).path(encodedParameter).path(Integer.toString(templateId));
			LOG.debug("Invoking: {}", request.getUri());

			String result = request.request(MediaType.TEXT_PLAIN).get(String.class);

			if (result == null || result.length() == 0) {
				LOG.error("Cannot find Component Presentation for locale: " + queryLocale + ", keyValueMap: " + keyValueMap);
				return new String[0];
			}

			String[] resultList = processResults(result);

			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching Component Presentation. Duration {}s", time / 1000.0);

			return resultList;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	private String appendSlashIfRequired (final String locale) {
		if (!locale.startsWith("/")) {
			return "/" + locale;
		}
		return locale;
	}

	@Override
	public String[] getDynamicComponentPresentationsBySchema (final String locale, final String schema, final int templateId) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching Component Presentation by Schema and by locale: {}, with Schema Name: {}, and template Id: {}", new Object[]{locale, schema, templateId});

		try {
			String queryLocale = appendSlashIfRequired(locale);
			WebTarget request = client.getQueryComponentBySchema().path(encodeUrl(queryLocale.toLowerCase()).trim()).path(encodeUrl(schema).trim()).path(Integer.toString(templateId));
			LOG.debug("Invoking: {}", request.getUri());

			String result = request.request(MediaType.TEXT_PLAIN).get(String.class);

			if (result == null || result.length() == 0) {
				LOG.error("Cannot find Component Presentations for locale and schema: " + queryLocale + ", schema: " + schema);
				return new String[0];
			}

			String[] resultList = processResults(result);
			time = System.currentTimeMillis() - time;

			LOG.debug("Finished fetching Component Presentation. Duration {}ms", time / 1000.0);
			return resultList;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	@Override public String[] getDynamicComponentPresentationsBySchemaInKeyword (final String locale, final String schema, final int categoryId, final int keywordId, final int templateId) throws ItemNotFoundException, SerializationException {
		long time = System.currentTimeMillis();
		LOG.debug("Fetching Component Presentation by Schema and by locale: {}, with Schema Name: {}, in keyword: {} and template Id: {}", new Object[]{locale, schema, keywordId, templateId});

		try {
			String queryLocale = appendSlashIfRequired(locale);

			WebTarget request = client.getQueryComponentsBySchemaInKeyword().path(encodeUrl(queryLocale.toLowerCase()).trim()).path(encodeUrl(schema).trim()).path(Integer.toString(categoryId)).path(Integer.toString(keywordId)).path(Integer.toString(templateId));

			LOG.debug("Invoking: {}", request.getUri());

			// Request the Physical Content from Service
			String result = request.request(MediaType.TEXT_PLAIN).get(String.class);

			if (result == null || result.length() == 0) {
				LOG.info("Cannot find Component Presentations for locale: " + queryLocale + ", schema: " + schema + ", and keyword: " + keywordId);
				return new String[0];
			}

			// Split The result by | and decode the content
			String[] resultList = processResults(result);


			time = System.currentTimeMillis() - time;
			LOG.debug("Finished fetching Component Presentation. Duration {}ms", time / 1000.0);
			return resultList;
		} catch (NotFoundException nfe) {
			throw new ItemNotFoundException(nfe);
		} catch (ClientErrorException | ProcessingException e) {
			throw new SerializationException(e);
		}
	}

	private String[] processResults (final String result) throws SerializationException {
		String[] resultList = result.split("\\|");
		LOG.debug("Number of results: {}", resultList.length);
		for (int i = 0; i < resultList.length; i++) {
			resultList[i] = decodeAndDecompressContent(resultList[i]);
		}
		return resultList;
	}
}
