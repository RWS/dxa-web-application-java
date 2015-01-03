package org.dd4t.mvc.tags;

/**
 * dd4t-2
 *
 * NOTE: these tags are defined in dd4t.tld and are copied into the
 * META-INF jar for dd4t-mvc-support, so you won't have to register this
 * in your own web app.
 *
 * @author R. Kempees
 */

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.util.RenderUtils;

import java.util.List;

/**
 * ComponentPresentationsTag
 *
 * Renders only those component presentations which have the specified region set.
 *
 * If no region is set on the Tag, it will remove the component presentations
 * which do have a region set, in order to prevent double rendering on the page.
 *
 * @author R. Kempees, Q. Slings
 */
public class ComponentPresentationsTag extends BaseComponentPresentationsTag {
	private String region;

	@Override
	protected List<ComponentPresentation> getComponentPresentationsForRegion(Page page) {
		return RenderUtils.filterComponentPresentationsByRegion(page.getComponentPresentations(), this.region);
	}
	public String getRegion () {
		return region;
	}

	public void setRegion (final String region) {
		this.region = region;
	}
}