package org.dd4t.mvc.tags;

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.exceptions.FactoryException;
import org.dd4t.core.exceptions.ItemNotFoundException;
import org.dd4t.core.util.Constants;
import org.dd4t.core.util.RenderUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

/**
 * Components presentation rendering tag.
 */
public abstract class BaseComponentPresentationsTag extends SimpleTagSupport {

	private static final Logger LOG = LoggerFactory.getLogger(BaseComponentPresentationsTag.class);

	private String schema;
	private String rootElement;
	private String view;
	private Boolean addAnchor = true;
	private Integer start;
	private Integer end;

	public int getAnchorCount(HttpServletRequest request) {
		int counter = 0;

		if (null != request.getAttribute("anchorCounter")) {
			counter = (Integer) request.getAttribute("anchorCounter") + 1;
		}
		request.setAttribute("anchorCounter", counter);
		return counter;
	}

	protected abstract List<ComponentPresentation> getComponentPresentationsForRegion(Page page);

	@Override
	public void doTag() throws JspException, IOException {
		final Page page = (Page) getJspContext().getAttribute(Constants.PAGE_MODEL_KEY, PageContext.REQUEST_SCOPE);

		if (page != null) {
			final PageContext pageContext = (PageContext) getJspContext();
			List<ComponentPresentation> filteredComponentPresentations = RenderUtils.filterComponentPresentations(getComponentPresentationsForRegion(page), getSchema(), getRootElement(), getView());
			String out = "";

			if (start != null || end != null) {
				int size = filteredComponentPresentations.size();
				int startPos = (start != null) ? start : 0;
				int endPos = (end != null) ? end : size;

				if (startPos <= endPos && startPos <= size && endPos <= size) {
					filteredComponentPresentations = filteredComponentPresentations.subList(startPos, endPos);
				} else {
					LOG.error("start {} and end {} filtering incorrect for number of component presentations ({}) on page {}", size, page.getId());
				}
			}

			final HttpServletRequest request = (HttpServletRequest) pageContext.getRequest();
			final HttpServletResponse response = (HttpServletResponse) pageContext.getResponse();

			try {
				out = RenderUtils.renderComponentPresentations(request, response, filteredComponentPresentations);
			} catch (ItemNotFoundException | FactoryException e) {
				LOG.error(e.getLocalizedMessage(),e);
			}

			if (addAnchor) {
				// TODO: pageContext.getOut().write(String.format(ANCHOR_FORMAT, getAnchorCount(request)) + out);
				pageContext.getOut().write(out);
			} else {
				pageContext.getOut().write(out);
			}
		} else {
			LOG.warn("The JSP context does not contain an attribute called '" + Constants.PAGE_MODEL_KEY + "'.");
		}
	}

	public String getSchema() {
		return schema;
	}

	public void setSchema(final String schema) {
		this.schema = schema.toLowerCase(Locale.getDefault());
	}

	public String getRootElement() {
		return rootElement;
	}

	public void setRootElement(final String rootElement) {
		this.rootElement = rootElement;
	}

	public String getView() {
		return view;
	}

	public void setView(final String view) {
		this.view = view.toLowerCase(Locale.getDefault());
	}

	public Boolean isAddAnchor() {
		return addAnchor;
	}

	public void setAddAnchor(final Boolean addAnchor) {
		this.addAnchor = addAnchor;
	}

	public void setStart(final Integer start) {
		this.start = start;
	}

	public void setEnd(final Integer end) {
		this.end = end;
	}
}
