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

package org.dd4t.mvc.tags;

import org.dd4t.core.factories.PageFactory;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.HttpUtils;
import org.dd4t.mvc.utils.PublicationResolverFactory;
import org.dd4t.mvc.utils.RenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.DispatcherType;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;
import java.io.IOException;
import java.net.URL;

/**
 * SmartInclude Tag. JSP Tag which takes a 'page' parameter to include in the
 * current JSP page.
 * <p/>
 * If the page is not found in the given path and the page attribute on the tag does not start
 * with a slash, the tag logic will
 * search the parent paths one by one until a match is found. Existence will first be
 * checked whether this is the case, in order to prevent heavy loading.
 * <p/>
 * Since include pages are published through Tridion, the DD4T PageFactory is used.
 * This also means that full DD4T rendering is initiated.
 * <p/>
 * Important note: if the to be included page uses the same view as the 'parent' page,
 * Spring MVC will throw an error, because of the danger of having circular view inclusions,
 * and thus infinite loops. Therefore, a different view must always be used.
 * <p/>
 * Usage <t:smartInclude page="/includetest.html"/>
 *
 * @author R. Kempees
 */
public class SmartIncludeTag extends TagSupport {

    private static final Logger LOG = LoggerFactory.getLogger(SmartIncludeTag.class);
    private String page;
    private transient PublicationResolver publicationResolver = PublicationResolverFactory.getPublicationResolver();

    @Override
    public int doStartTag () throws JspException {
        final HttpServletRequest currentRequest = (HttpServletRequest) pageContext.getRequest();

        if (currentRequest.getDispatcherType() == DispatcherType.INCLUDE || currentRequest.getAttribute(Constants.SMART_INCLUDE_URL) != null) {
            LOG.debug("Already including.");
            return SKIP_BODY;
        }

        String includeUrl = page;

        final PageFactory pageFactory = PageFactoryImpl.getInstance();
        try {
            final int publicationId = publicationResolver.getPublicationId();
            boolean pageFound;

            // prepend the current request URL
            // Test if the inclusion exists. If not, move up the tree
            final String requestUrl = HttpUtils.getOriginalUri(currentRequest);
            // We do this to prevent query string hassles and not have other edge cases.
            final URL aUrl = new URL(String.format("http://localhost%s", requestUrl));
            String testPath = aUrl.getPath();


            if (testPath.endsWith(".html") || testPath.endsWith(".xml") || testPath.endsWith(".txt")) {
                testPath = testPath.substring(0, testPath.lastIndexOf("/"));
            }

            if (testPath.equals("/") && !includeUrl.startsWith("/")) {
                includeUrl = "/" + includeUrl;
            }

            if (includeUrl.startsWith("/")) {
                pageFound = pageFactory.isPagePublished(includeUrl, publicationId);
            } else {
                LOG.debug("Current path={}", testPath);

                String url = String.format("%s/%s", testPath, includeUrl);
                pageFound = pageFactory.isPagePublished(url, publicationId);

                if (pageFound) {
                    includeUrl = url;
                } else {
                    while (testPath.length() > 0 && !pageFound) {
                        if (testPath.length() > 1) {
                            testPath = testPath.substring(0, testPath.lastIndexOf("/"));
                        }

                        url = String.format("%s/%s", testPath, includeUrl);

                        LOG.debug("Testing URL {}", url);

                        if (pageFactory.isPagePublished(url, publicationId)) {
                            pageFound = true;
                            includeUrl = url;
                        }
                    }
                }
            }

            if (!pageFound) {
                return SKIP_BODY;
            }
            includePage(currentRequest, includeUrl);
        } catch (IOException | ServletException e) {
            LOG.error(e.getMessage(), e);
            throw new JspException(e);
        }

        return SKIP_BODY;
    }

    /**
     * Performs the actual page inclusion by using the current
     * RequestDispatcher. When inclusion is performed, the Smart Include URL is reset to prevent circular inclusions.
     *
     * @param currentRequest the current HttpServletRequest
     * @param includeUrl     the final determined URL to include
     * @throws ServletException
     * @throws IOException
     */
    private void includePage (final HttpServletRequest currentRequest, final String includeUrl) throws ServletException, IOException {
        pageContext.getRequest().setAttribute(Constants.SMART_INCLUDE_URL, includeUrl);
        LOG.debug(">> Including: {}", includeUrl);

        String renderedInclude = RenderUtils.dispatchBufferedRequest(currentRequest, (HttpServletResponse) this.pageContext.getResponse(), includeUrl);
        pageContext.getOut().print(renderedInclude);

        currentRequest.setAttribute(Constants.SMART_INCLUDE_URL, null);
        LOG.debug("<< End including {}", includeUrl);
    }

    /**
     * @return int
     * @throws JspException
     * @see super.doEndTag()
     */
    @Override
    public int doEndTag () throws JspException {
        return SKIP_BODY;
    }

    /**
     * Gets the set page URL
     *
     * @return String the URL
     */
    public String getPage () {
        return page;
    }

    /**
     * Set page url to include. Must be a published DD4T Tridion URL
     *
     * @param page the page URL to include
     */
    public void setPage (final String page) {
        this.page = page;
    }
}
