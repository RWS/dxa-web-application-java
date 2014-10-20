package com.sdl.webapp.tridion;

import com.google.common.base.Strings;
import com.tridion.dynamiccontent.publication.PublicationMapping;

public final class PublicationMappingUtil {

    private static final String DEFAULT_PORT = "80";

    private PublicationMappingUtil() {
    }

    public static String getPublicationMappingBaseUrl(PublicationMapping publicationMapping) {
        final StringBuilder sb = new StringBuilder();
        sb.append(publicationMapping.getProtocol()).append("://").append(publicationMapping.getDomain());

        final String port = publicationMapping.getPort();
        if (!DEFAULT_PORT.equals(port)) {
            sb.append(':').append(port);
        }

        return sb.append(getPublicationMappingPath(publicationMapping)).toString();
    }

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
