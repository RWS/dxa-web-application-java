package com.sdl.webapp.common.impl.localization;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.SiteLocalization;
import com.sdl.webapp.common.api.mapping.config.SemanticSchema;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Implementation of {@code Localization}.
 */
class LocalizationImpl implements Localization {

    public static final class Builder {
        private String id;
        private String path;
        private String mediaRoot;
        private boolean default_;
        private boolean staging;
        private String version;

        private final ImmutableList.Builder<SiteLocalization> siteLocalizationsBuilder = ImmutableList.builder();
        private final ImmutableMap.Builder<String, String> configurationBuilder = ImmutableMap.builder();
        private final ImmutableMap.Builder<String, String> resourcesBuilder = ImmutableMap.builder();
        private final ImmutableMap.Builder<Long, SemanticSchema> semanticSchemasBuilder = ImmutableMap.builder();
        private final ImmutableListMultimap.Builder<String, String> includesBuilder = ImmutableListMultimap.builder();

        private Builder() {
        }

        public Builder setId(String id) {
            this.id = id;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setMediaRoot(String mediaRoot) {
            this.mediaRoot = mediaRoot;
            return this;
        }

        public Builder setDefault(boolean default_) {
            this.default_ = default_;
            return this;
        }

        public Builder setStaging(boolean staging) {
            this.staging = staging;
            return this;
        }

        public Builder setVersion(String version) {
            this.version = version;
            return this;
        }

        public Builder addSiteLocalizations(Iterable<? extends SiteLocalization> siteLocalizations) {
            this.siteLocalizationsBuilder.addAll(siteLocalizations);
            return this;
        }

        public Builder addConfiguration(Map<String, String> configuration) {
            this.configurationBuilder.putAll(configuration);
            return this;
        }

        public Builder addResources(Map<String, String> resources) {
            this.resourcesBuilder.putAll(resources);
            return this;
        }

        public Builder addSemanticSchema(SemanticSchema semanticSchema) {
            this.semanticSchemasBuilder.put(semanticSchema.getId(), semanticSchema);
            return this;
        }

        public Builder addSemanticSchemas(Iterable<SemanticSchema> semanticSchemas) {
            for (SemanticSchema semanticSchema : semanticSchemas) {
                addSemanticSchema(semanticSchema);
            }
            return this;
        }

        public Builder addInclude(String pageTypeId, String include) {
            this.includesBuilder.put(pageTypeId, include);
            return this;
        }

        public Localization build() {
            return new LocalizationImpl(this);
        }
    }

    private static final String FAVICON_PATH = "/favicon.ico";

    private static final Pattern SYSTEM_ASSETS_PATTERN = Pattern.compile("/system(/v\\d+\\.\\d+)?/assets/.*");

    private final String id;
    private final String path;
    private final String mediaRoot;
    private final boolean default_;
    private final boolean staging;
    private final String version;

    private final List<SiteLocalization> siteLocalizations;
    private final Map<String, String> configuration;
    private final Map<String, String> resources;
    private final Map<Long, SemanticSchema> semanticSchemas;
    private final ListMultimap<String, String> includes;

    private LocalizationImpl(Builder builder) {
        this.id = builder.id;
        this.path = builder.path;
        this.mediaRoot = builder.mediaRoot;
        this.default_ = builder.default_;
        this.staging = builder.staging;
        this.version = builder.version;

        this.siteLocalizations = builder.siteLocalizationsBuilder.build();
        this.configuration = builder.configurationBuilder.build();
        this.resources = builder.resourcesBuilder.build();
        this.semanticSchemas = builder.semanticSchemasBuilder.build();
        this.includes = builder.includesBuilder.build();
    }

    public static Builder newBuilder() {
        return new Builder();
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
        if (url.startsWith(mediaRoot)) {
            return true;
        }

        final String p = path.equals("/") ? url : url.substring(path.length());
        return p.equals(FAVICON_PATH) || SYSTEM_ASSETS_PATTERN.matcher(p).matches();
    }

    @Override
    public boolean isDefault() {
        return default_;
    }

    @Override
    public boolean isStaging() {
        return staging;
    }

    @Override
    public String getVersion() {
        return version;
    }

    @Override
    public String getCulture() {
        return getConfiguration("core.culture");
    }

    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(getCulture());
    }

    @Override
    public List<SiteLocalization> getSiteLocalizations() {
        return siteLocalizations;
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
    public List<String> getIncludes(String pageTypeId) {
        return includes.get(pageTypeId);
    }

    @Override
    public String localizePath(String url) {
        if (!Strings.isNullOrEmpty(path)) {
            if (path.endsWith("/")) {
                url = path + (url.startsWith("/") ? url.substring(1) : url);
            } else {
                url = path + (url.startsWith("/") ? url : "/" + url);
            }
        }
        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LocalizationImpl that = (LocalizationImpl) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (path != null ? !path.equals(that.path) : that.path != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (path != null ? path.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "LocalizationImpl{" +
                "id='" + id + '\'' +
                ", path='" + path + '\'' +
                ", mediaRoot='" + mediaRoot + '\'' +
                ", default_=" + default_ +
                ", staging=" + staging +
                ", configuration=" + configuration +
                ", resources=" + resources +
                ", semanticSchemas=" + semanticSchemas +
                ", includes=" + includes +
                '}';
    }
}
