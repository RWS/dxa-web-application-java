package org.example.service;

import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Admin service that provides admin actions.
 */
@Slf4j
@Service
public class AdminService {

    @Autowired
    private WebRequestContext webRequestContext;

    @Autowired
    private LocalizationResolver localizationResolver;

    /**
     * Refreshes the localization and returns the index path.
     *
     * @return the index path of the localization
     */
    public String refreshLocalization() {
        final Localization localization = webRequestContext.getLocalization();

        log.trace("handleRefresh: localization {}", localization);

        localizationResolver.refreshLocalization(localization);
        return localization.getPath();
    }
}
