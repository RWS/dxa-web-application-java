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
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.resolvers.LinkResolver;
import org.dd4t.core.util.RichTextUtils;

import javax.annotation.Resource;
import javax.xml.transform.TransformerException;

/**
 * Filter to resolve component links.
 *
 * @author bjornl
 */
public class RichTextWithLinksResolver extends RichTextResolver implements Processor {

    @Resource
    private LinkResolver linkResolver;

    @Override
    protected void resolveXhtmlField (XhtmlField xhtmlField) throws TransformerException {
        try {
            RichTextUtils.resolveXhtmlField(xhtmlField, true, this.linkResolver, this.linkResolver.getContextPath());
        } catch (ItemNotFoundException | SerializationException e) {
            throw new TransformerException(e);
        }
    }

    public LinkResolver getLinkResolver () {
        return linkResolver;
    }

    public void setLinkResolver (LinkResolver linkResolver) {
        this.linkResolver = linkResolver;
    }
}