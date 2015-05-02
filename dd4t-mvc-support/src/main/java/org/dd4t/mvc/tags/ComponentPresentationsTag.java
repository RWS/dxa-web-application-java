package org.dd4t.mvc.tags;

/**
 * dd4t-2
 *
 * NOTE: these tags are defined in dd4t.tld and are copied into the
 * META-INF directory in the jar for dd4t-mvc-support, so you won't have to register this
 * in your own web app.
 *
 * @author R. Kempees
 */

import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;

import java.util.List;

/**
 * ComponentPresentationsTag
 *
 * @author R. Kempees, Q. Slings
 */
public class ComponentPresentationsTag extends BaseComponentPresentationsTag {
	@Override
	protected List<ComponentPresentation> getComponentPresentations(Page page) {
		return page.getComponentPresentations();
	}

}