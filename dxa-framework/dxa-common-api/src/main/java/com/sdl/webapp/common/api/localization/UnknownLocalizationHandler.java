package com.sdl.webapp.common.api.localization;

import javax.servlet.ServletRequest;

/**
 * <p>Interface for Unknown Localization Handler extension point.</p>
 * <p>This extension point allows an implementation to handle requests for an unknown {@link Localization} and resolve it,
 * and optionally, in case localization is not resolvable, provide a fallback exception
 * that will be used to render error page instead of the default HTTP 404 with a plain-text error message.</p>
 * @dxa.publicApi
 */
public interface UnknownLocalizationHandler {

    /**
     * Handles a Request for an Unknown Localization (i.e. the request URL doesn't map to a Publication).
     *
     * @param exception exception occurred during <i>normal</i> attempt to resolve localization
     * @param request   the current request
     * @return may return a {@link Localization} instance if the handler manages to resolve the Localization.
     * In case of <code>null</code> is returned, that means there is no any available localization
     */
    Localization handleUnknown(Exception exception, ServletRequest request);

    /**
     * Returns an instance of fallback exception when it's impossible to resolve the {@link Localization}.
     *
     * @param exception initial exception when failed to resolve a {@link Localization}
     * @param request   the current request
     * @return a fallback exception to use instead of default, in case <code>null</code> is returned, fallback to default
     */
    LocalizationNotResolvedException getFallbackException(Exception exception, ServletRequest request);
}
