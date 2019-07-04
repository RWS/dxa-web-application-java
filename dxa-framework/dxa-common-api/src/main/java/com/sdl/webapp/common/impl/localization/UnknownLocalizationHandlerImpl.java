package com.sdl.webapp.common.impl.localization;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationNotResolvedException;
import com.sdl.webapp.common.api.localization.UnknownLocalizationHandler;
import com.sdl.webapp.common.impl.localization.DocsLocalization;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.ServletRequest;

/**
 * Unknown localization handler.
 */
@Component
@Slf4j
public class UnknownLocalizationHandlerImpl implements UnknownLocalizationHandler {

    /**
     * Handle unknown localization. Happens when there is no publication mapped to the domain.
     * @param exception exception occurred during <i>normal</i> attempt to resolve localization
     * @param request   the current request
     * @return  defaul localization for module
     */
    public Localization handleUnknown(Exception exception, ServletRequest request) {
        // Create a new localization object on every request
        // This cannot be static as the publication id can be different on any request
        return new DocsLocalization();
    }

    /**
     * Fallback exception in case the dita localization also failed.
     * @param exception initial exception when failed to resolve a {@link Localization}
     * @param request   the current request
     * @return Exception to fire spring to show error page
     */
    public LocalizationNotResolvedException getFallbackException(Exception exception, ServletRequest request) {
        return new LocalizationNotResolvedException.WithCustomResponse("Exception with JSON", HttpStatus.OK.value(),
                "{\"message\":\"Localization problem!\"}", "application/json");
    }
}
