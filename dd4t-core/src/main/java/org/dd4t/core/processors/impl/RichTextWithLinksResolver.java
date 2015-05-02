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

package org.dd4t.core.processors.impl;

import org.dd4t.contentmodel.impl.XhtmlField;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.resolvers.LinkResolver;
import org.dd4t.core.util.XSLTransformer;

import javax.annotation.Resource;
import javax.xml.transform.TransformerException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Filter to resolve component links.
 *
 * @author bjornl
 */
public class RichTextWithLinksResolver extends RichTextResolver implements Processor {

	private final XSLTransformer xslTransformer = XSLTransformer.getInstance();

	@Resource
	private LinkResolver linkResolver;

	@Override
	protected void resolveXhtmlField (XhtmlField xhtmlField) throws TransformerException {
		List<Object> xhtmlValues = xhtmlField.getValues();
		List<String> newValues = new ArrayList<>();

		// get context path from linkResolver
		Map<String, Object> params = new HashMap<>();
		params.put("contextpath", linkResolver.getContextPath());

		// find all component links and try to resolve them
		Pattern p = Pattern.compile("</?ddtmproot>");
		for (Object xhtmlValue : xhtmlValues) {
			String result = xslTransformer.transformSourceFromFilesource("<ddtmproot>" + xhtmlValue + "</ddtmproot>", "/resolveXhtmlWithLinks.xslt", params);
			newValues.add(p.matcher(result).replaceAll(""));
		}
		xhtmlField.setTextValues(newValues);
	}

	public LinkResolver getLinkResolver () {
		return linkResolver;
	}

	public void setLinkResolver (LinkResolver linkResolver) {
		this.linkResolver = linkResolver;
	}
}