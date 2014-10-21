package com.sdl.webapp.common.impl;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.Localization;
import com.sdl.webapp.common.api.mapping.SemanticSchema;
import com.sdl.webapp.common.api.mapping.SemanticVocabulary;

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

    private boolean default_;

    private boolean staging;

    private Map<String, String> configuration;

    private Map<String, String> resources;

    private Map<Long, SemanticSchema> semanticSchemas;

    private List<SemanticVocabulary> semanticVocabularies;

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
    public boolean isDefault() {
        return default_;
    }

    public void setDefault(boolean default_) {
        this.default_ = default_;
    }

    @Override
    public boolean isStaging() {
        return staging;
    }

    public void setStaging(boolean staging) {
        this.staging = staging;
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
    public Map<Long, SemanticSchema> getSemanticSchemas() {
        return semanticSchemas;
    }

    @Override
    public List<SemanticVocabulary> getSemanticVocabularies() {
        return semanticVocabularies;
    }

    @Override
    public List<String> getIncludes(String pageTypeId) {
        return includes.get(pageTypeId);
    }

    public void setMediaRoot(String mediaRoot) {
        this.mediaRoot = mediaRoot;
    }

    public void setConfiguration(Map<String, String> configuration) {
        this.configuration = ImmutableMap.copyOf(configuration);
    }

    public void setResources(Map<String, String> resources) {
        this.resources = ImmutableMap.copyOf(resources);
    }

    public void setSemanticSchemas(Map<Long, SemanticSchema> semanticSchemas) {
        this.semanticSchemas = ImmutableMap.copyOf(semanticSchemas);
    }

    public void setSemanticVocabularies(List<SemanticVocabulary> semanticVocabularies) {
        this.semanticVocabularies = ImmutableList.copyOf(semanticVocabularies);
    }

    public void setIncludes(ListMultimap<String, String> includes) {
        this.includes = ImmutableListMultimap.copyOf(includes);
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
