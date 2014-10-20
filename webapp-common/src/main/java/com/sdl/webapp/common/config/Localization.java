package com.sdl.webapp.common.config;

import com.google.common.base.Strings;
import com.google.common.collect.ListMultimap;

import java.util.List;
import java.util.Map;

/**
 * Information about a localization.
 */
public class Localization {

    private static final String FAVICON_PATH = "/favicon.ico";
    private static final String SYSTEM_ASSETS_PATH = "/system/assets";

    private final int publicationId;
    private final String path;
    private final String mediaRoot;

    private final Map<String, String> configuration;
    private final Map<String, String> resources;
    private final ListMultimap<String, String> includes;

    public Localization(int publicationId, String path, String mediaRoot, Map<String, String> configuration,
                        Map<String, String> resources, ListMultimap<String, String> includes) {
        this.publicationId = publicationId;

        // Make sure path starts with and does not end with a slash
        path = Strings.nullToEmpty(path);
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.endsWith("/")) {
            path = path.substring(0, path.length() - 1);
        }
        this.path = path;

        this.mediaRoot = mediaRoot;
        this.configuration = configuration;
        this.resources = resources;
        this.includes = includes;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public String getPath() {
        return path;
    }

    public boolean isStaticResourceUrl(String url) {
        if (!url.startsWith(path)) {
            return false;
        }

        final String p = url.substring(path.length());
        return p.equals(FAVICON_PATH) || p.startsWith(SYSTEM_ASSETS_PATH) || p.startsWith(mediaRoot);
    }

    public String getConfiguration(String key) {
        return configuration.get(key);
    }

    public String getResource(String key) {
        return resources.get(key);
    }

    public List<String> getIncludes(String pageTypeId) {
        return includes.get(pageTypeId);
    }

    @Override
    public String toString() {
        return "Localization{" +
                "publicationId=" + publicationId +
                ", path='" + path + '\'' +
                ", mediaRoot='" + mediaRoot + '\'' +
                ", configuration=" + configuration +
                ", resources=" + resources +
                '}';
    }
}
