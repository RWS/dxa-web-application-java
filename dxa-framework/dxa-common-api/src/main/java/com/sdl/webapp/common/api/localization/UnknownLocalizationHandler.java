package com.sdl.webapp.common.api.localization;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Interface for Unknown Localization Handler extension point.
 * <p>This extension points allows an implementation to intercept request for an Unknown Localization and optionally provide a different HTTP Response
 * than the default HTTP 404 with a plain-text error message.</p>
 */
public interface UnknownLocalizationHandler {

    /**
     * Handles a Request for an Unknown Localization (i.e. the request URL doesn't map to a Publication).
     *
     * @param exception exception occurred during <i>normal</i> attempt to resolve localization
     * @param request   the current request
     * @param response  the current response
     * @return may return a {@link Localization} instance if the handler manages to resolve the Localization.
     * In case of <code>null</code> is returned, that means there is no any available localization
     */
    Localization handleUnknown(Exception exception, ServletRequest request, ServletResponse response);
}
