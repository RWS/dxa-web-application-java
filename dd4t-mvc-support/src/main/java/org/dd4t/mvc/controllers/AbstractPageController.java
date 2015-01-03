package org.dd4t.mvc.controllers;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.core.factories.impl.PropertiesServiceFactory;
import org.dd4t.core.resolvers.PublicationResolver;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.HttpUtils;
import org.dd4t.core.util.RenderUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.TimeZone;

/**
 * dd4t-2
 *
 * Extend this class in your own web project for default functionality.
 *
 * Do NOT add stuff here, as this will in the near future be loaded through maven only!
 *
 * @author R. Kempees
 */
@Controller
public abstract class AbstractPageController {
	private static final Logger LOG = LoggerFactory.getLogger(AbstractPageController.class);
	private static final TimeZone GMT = TimeZone.getTimeZone("GMT");
	private static final String LAST_MODIFIED = "Last-Modified";
	private static final String DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz";
	@Autowired
	private PageFactoryImpl pageFactory;
	@Autowired
	private PublicationResolver publicationResolver;

	private String pageViewPath = "";

	/**
	 * Boolean indicating if context path on the page URL should be removed, defaults to true
	 */
	private boolean removeContextPath = false;

	/**
	 * All page requests are handled by this method. The page meta XML is
	 * queried based on the request URI, the page meta XML contains the actual
	 * view name to be rendered.
	 */
	@RequestMapping(value = {"/**/*.html", "/**/*.txt", "/**/*.xml"}, method = {RequestMethod.GET, RequestMethod.HEAD})
	public String showPage(Model model, HttpServletRequest request, HttpServletResponse response) throws IOException {
		String originalUrl = HttpUtils.getCurrentURL(request);
		originalUrl = HttpUtils.appendDefaultPageIfRequired(originalUrl);

		String url = adjustLocalErrorUrl(request, originalUrl);
		url = HttpUtils.normalizeUrl(url);

		LOG.debug(">> {} page {} with dispatcher type {}", new Object[]{request.getMethod(), url, request.getDispatcherType().toString()});
		try {
			if (StringUtils.isEmpty(url)) {
				// url is not valid, throw an ItemNotFoundException
				throw new ItemNotFoundException("Local Page Url could not be resolved: " + originalUrl + " (probably publication url could not be resolved)");
			}

			Page pageModel = pageFactory.findPageByUrl(url, publicationResolver.getPublicationId());

			DateTime lastPublishDate = pageModel.getLastPublishedDate();

			response.setHeader(LAST_MODIFIED, createDateFormat().format(lastPublishDate.toDate()));

			model.addAttribute(Constants.REFERER, request.getHeader(HttpHeaders.REFERER));
			model.addAttribute(Constants.PAGE_MODEL_KEY, pageModel);
			model.addAttribute(Constants.PAGE_REQUEST_URI, HttpUtils.appendDefaultPageIfRequired(request.getRequestURI()));

			response.setContentType(HttpUtils.getContentTypeByExtension(url));
			String pageView = getPageViewName(pageModel);
			return pageView;

		} catch (ItemNotFoundException | FactoryException e) {

			LOG.warn("Page with url '{}' could not be found.", url);
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
//		} catch (Exception e) {
//			LOG.error("Generic Error.", e);
//			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//		}

		return null;
	}

	private String getDefaultPublicationUrl() {
		return getPropertiesService().getProperty(Constants.DEFAULT_PUBLICATION_URL_CONFIGURATION_KEY);
	}


	private String adjustLocalErrorUrl(HttpServletRequest request, String url) {
		if (request.getDispatcherType() == DispatcherType.ERROR) {
			url = publicationResolver.getLocalPageUrl(url);
		}

		return url;
	}


	public String getPageViewName(final Page page) {
		String viewName;
		if (null != page.getPageTemplate().getMetadata() && page.getPageTemplate().getMetadata().containsKey("viewName")) {
			viewName = (String) page.getPageTemplate().getMetadata().get("viewName").getValues().get(0);
		} else {
			viewName = page.getPageTemplate().getTitle();
		}

		return RenderUtils.fixUrl(getPageViewPath() + viewName.trim());
	}

	/**
	 * @return the pageViewPrefix
	 */
	public String getPageViewPath() {
		return pageViewPath;
	}

	/**
	 *
	 */
	public void setPageViewPath(final String pageViewPath) {
		this.pageViewPath = pageViewPath;
	}

	public boolean isRemoveContextPath() {
		return removeContextPath;
	}

	public void setRemoveContextPath(boolean removeContextPath) {
		this.removeContextPath = removeContextPath;
	}

	/**
	 * Create Date format for last-modified headers. Note that a constant
	 * SimpleDateFormat is not allowed, it's access should be sync-ed.
	 */
	private DateFormat createDateFormat() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.US);
		dateFormat.setTimeZone(GMT);
		return dateFormat;
	}

	private PropertiesService getPropertiesService() {
		PropertiesServiceFactory factory = PropertiesServiceFactory.getInstance();
		return factory.getPropertiesService();
	}
}
