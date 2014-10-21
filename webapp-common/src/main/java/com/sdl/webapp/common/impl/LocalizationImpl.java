package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.Localization;

import java.util.List;
import java.util.Map;

/**
 * Default implementation of {@code Localization}.
 */
public class LocalizationImpl implements Localization {

    private static final String FAVICON_PATH = "/favicon.ico";
    private static final String SYSTEM_ASSETS_PATH = "/system/assets";

    private final String id;
    private final String path;

    private String mediaRoot;

    private Map<String, String> configuration;
    private Map<String, String> resources;
    private ListMultimap<String, String> includes;

    public LocalizationImpl(String id, String path) {
        this.id = id;
        this.path = path;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public boolean isStaticContent(String url) {
        if (!url.startsWith(path)) {
            return false;
        }

        final String p = url.substring(path.length());
        return p.equals(FAVICON_PATH) || p.startsWith(SYSTEM_ASSETS_PATH) || p.startsWith(mediaRoot);
    }

    @Override
    public String getConfiguration(String key) {
        return configuration.get(key);
    }

    @Override
    public String getResource(String key) {
        return resources.get(key);
    }

    @Override
    public List<String> getIncludes(String pageTypeId) {
        return includes.get(pageTypeId);
    }

    public void setMediaRoot(String mediaRoot) {
        this.mediaRoot = mediaRoot;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = configuration;
    }

    public void setResources(Map<String, String> resources) {
        this.resources = resources;
    }

    public void setIncludes(ListMultimap<String, String> includes) {
        this.includes = includes;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();
        sb.append('[').append(id).append(']');
        if (!Strings.isNullOrEmpty(path)) {
            sb.append(' ').append(path);
        }
        return sb.toString();
    }
}
