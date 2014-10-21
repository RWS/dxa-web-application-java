package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.tridion.dynamiccontent.publication.PublicationMapping;

/**
 * Utility methods for working with publication mappings.
 */
public final class PublicationMappingUtil {

    private static final String DEFAULT_PORT = "80";

    private PublicationMappingUtil() {
    }

    /**
     * Gets the base URL of a publication mapping.
     *
     * @param publicationMapping The publication mapping.
     * @return The base URL of the publication mapping.
     */
    public static String getPublicationMappingBaseUrl(PublicationMapping publicationMapping) {
        final StringBuilder sb = new StringBuilder();
        sb.append(publicationMapping.getProtocol()).append("://").append(publicationMapping.getDomain());

        final String port = publicationMapping.getPort();
        if (!DEFAULT_PORT.equals(port)) {
            sb.append(':').append(port);
        }

        return sb.append(getPublicationMappingPath(publicationMapping)).toString();
    }

    /**
     * Gets the publication mapping path. The returned path always starts with a "/" and does not end with a "/", unless
     * the path is the root path "/" itself.
     *
     * @param publicationMapping The publication mapping.
     * @return The publication mapping path.
     */
    public static String getPublicationMappingPath(PublicationMapping publicationMapping) {
        String path = Strings.nullToEmpty(publicationMapping.getPath());
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        return path;
    }
}
