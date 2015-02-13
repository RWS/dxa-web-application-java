package org.dd4t.core.processors;

import javax.servlet.http.HttpServletRequest;

/**
 * dd4t-2
 *
 * @author R. Kempees
 */
public interface ProcessorInstruction {
	HttpServletRequest getRequest();
	HttpServletRequest setRequest();
}
