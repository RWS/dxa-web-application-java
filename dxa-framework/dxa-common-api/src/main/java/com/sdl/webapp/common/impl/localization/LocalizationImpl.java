package com.sdl.webapp.common.impl.localization;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.SiteLocalization;
import com.sdl.webapp.common.api.mapping.semantic.config.SemanticSchema;
import com.sdl.webapp.common.util.FileUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

@EqualsAndHashCode
@ToString(of = {"id", "path"})
public class LocalizationImpl implements Localization {

    private static final String FAVICON_PATH = "/favicon.ico";
    private static final Pattern SYSTEM_ASSETS_PATTERN = Pattern.compile("/system(/v\\d+\\.\\d+)?/assets/.*");
    private static final Pattern SYSTEM_RESOURCES_PATTERN = Pattern.compile("/system(/v\\d+\\.\\d+)?/(resources|config)/.*");

    @Getter
    private final String id;

    @Getter
    private final String path;

    private final String mediaRoot;

    private final boolean default_;

    @Getter
    private final boolean staging;

    @Getter
    private final String version;

    @Getter
    private final boolean isHtmlDesignPublished;

    @Getter
    private final List<SiteLocalization> siteLocalizations;

    private final Map<String, String> configuration;

    private final Map<String, String> resources;

    @Getter
    private final Map<Long, SemanticSchema> semanticSchemas;

    private final ListMultimap<String, String> includes;

    private LocalizationImpl(Builder builder) {
        this.id = builder.id;
        this.path = builder.path;

        if (!builder.mediaRoot.startsWith("/")) {
            String strFormat = this.path.endsWith("/") ? "%s%s" : "%s/%s";
            this.mediaRoot = String.format(strFormat, this.path, builder.mediaRoot);
        } else {
            this.mediaRoot = builder.mediaRoot;
        }

        this.default_ = builder.default_;
        this.staging = builder.staging;
        this.version = builder.version;
        this.isHtmlDesignPublished = builder.htmlDesignPublished;
        this.siteLocalizations = builder.siteLocalizationsBuilder.build();
        this.configuration = builder.configurationBuilder.build();
        this.resources = builder.resourcesBuilder.build();
        this.semanticSchemas = builder.semanticSchemasBuilder.build();
        this.includes = builder.includesBuilder.build();

    }

    /**
     * <p>newBuilder.</p>
     *
     * @return a {@link Builder} object.
     */
    public static Builder newBuilder() {
        return new Builder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isStaticContent(String url) {
        if (!url.startsWith(path)) {
            return false;
        }
        if (url.startsWith(mediaRoot)) {
            return true;
        }
        final String p = path.equals("/") ? url : url.substring(path.length());
        if (SYSTEM_RESOURCES_PATTERN.matcher(p).matches()) {
            return true;
        }
        return FileUtils.isFavicon(p) || SYSTEM_ASSETS_PATTERN.matcher(p).matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isNonPublishedAsset(String url) {
        return !this.isHtmlDesignPublished && SYSTEM_ASSETS_PATTERN.matcher(url).matches();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefault() {
        return default_;
    }

    /** {@inheritDoc} */
    @Override
    public String getCulture() {
        return getConfiguration("core.culture");
    }

    /** {@inheritDoc} */
    @Override
    public Locale getLocale() {
        return Locale.forLanguageTag(getCulture());
    }

    /** {@inheritDoc} */
    @Override
    public String getConfiguration(String key) {
        return configuration.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public String getResource(String key) {
        return resources.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public List<String> getIncludes(String pageTypeId) {
        return includes.get(pageTypeId);
    }

    /** {@inheritDoc} */
    @Override
    public String localizePath(String url) {
        if (!Strings.isNullOrEmpty(path)) {
            if (path.endsWith("/")) {
                url = path + (url.startsWith("/") ? url.substring(1) : url);
            } else {
                url = path + (url.startsWith("/") ? url : '/' + url);
            }
        }
        return url;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<String> getDataFormats() {
        String[] formats = getConfiguration("core.dataFormats").split("(\\s*)?,(\\s*)?");
        return Arrays.asList(formats);
    }

    public static final class Builder {
        private final ImmutableList.Builder<SiteLocalization> siteLocalizationsBuilder = ImmutableList.builder();
        private final ImmutableMap.Builder<String, String> configurationBuilder = ImmutableMap.builder();
        private final ImmutableMap.Builder<String, String> resourcesBuilder = ImmutableMap.builder();
        private final ImmutableMap.Builder<Long, SemanticSchema> semanticSchemasBuilder = ImmutableMap.builder();
        private final ImmutableListMultimap.Builder<String, String> includesBuilder = ImmutableListMultimap.builder();
        private String id;
        private String path;
        private String mediaRoot;
        private boolean default_;
        private boolean staging;
        private String version;
        private boolean htmlDesignPublished;

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

        public Builder setHtmlDesignPublished(boolean htmlDesignPublished) {
            this.htmlDesignPublished = htmlDesignPublished;
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
}
