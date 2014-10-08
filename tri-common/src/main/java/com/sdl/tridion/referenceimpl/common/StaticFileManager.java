package com.sdl.tridion.referenceimpl.common;

import java.io.File;
import java.io.IOException;

/**
 * TODO: Documentation.
 */
public interface StaticFileManager {

    /**
     * Gets static content for the specified URL and stores it in the specified file.
     *
     * @param url The URL.
     * @param destinationFile The file to store the static content in.
     * @param publicationId The publication id.
     * @return {@code true} if there is static content for the specified URL available and it was saved in the file,
     *          {@code false} otherwise.
     * @throws IOException If an I/O error occurs.
     */
    boolean getStaticContent(String url, File destinationFile, int publicationId) throws IOException;

    /**
     * Gets static content for the specified URL and stores it in the specified file, using the current publication id.
     *
     * @param url The URL.
     * @param destinationFile The file to store the static content in.
     * @return {@code true} if there is static content for the specified URL available and it was saved in the file,
     *          {@code false} otherwise.
     * @throws IOException If an I/O error occurs.
     */
    boolean getStaticContent(String url, File destinationFile) throws IOException;
}
