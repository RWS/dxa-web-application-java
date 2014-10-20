package com.sdl.webapp.common;

import com.sdl.webapp.common.config.WebRequestContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;

/**
 * Abstract implementation of {@code StaticContentProvider} with common functionality.
 */
public abstract class AbstractStaticContentProvider implements StaticContentProvider {

    @Autowired
    private WebRequestContext webRequestContext;

    /**
     * Gets static content for the specified URL and stores it in the specified file, using the current publication id.
     * The current publication id is determined from the {@code WebRequestContext}.
     *
     * @param url The URL.
     * @param destinationFile The file to store the static content in.
     * @return {@code true} if there is static content for the specified URL available and it was saved in the file,
     *          {@code false} otherwise.
     * @throws IOException If an I/O error occurs.
     */
    @Override
    public boolean getStaticContent(String url, File destinationFile) throws IOException {
        return getStaticContent(url, destinationFile, webRequestContext.getPublicationId());
    }
}
