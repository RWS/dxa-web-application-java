package com.sdl.tridion.referenceimpl.common.config;

import com.google.common.base.Strings;

import java.util.Locale;

public final class Localization {

    public static final class Builder {
        private int localizationId;
        private String domain;
        private Integer port;
        private String path;
        private String protocol;
        private Locale locale;

        private Builder() {
        }

        public Builder setLocalizationId(int localizationId) {
            this.localizationId = localizationId;
            return this;
        }

        public Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder setPort(Integer port) {
            this.port = port;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setLocale(Locale locale) {
            this.locale = locale;
            return this;
        }

        public Localization build() {
            return new Localization(this);
        }
    }

    private final int localizationId;
    private final String domain;
    private final Integer port;
    private final String path;
    private final String protocol;
    private final Locale locale;

    private Localization(Builder builder) {
        this.localizationId = builder.localizationId;
        this.domain = builder.domain;
        this.port = builder.port;
        this.path = builder.path;
        this.protocol = builder.protocol;
        this.locale = builder.locale;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getLocalizationId() {
        return localizationId;
    }

    public String getDomain() {
        return domain;
    }

    public Integer getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Locale getLocale() {
        return locale;
    }

    public String getBaseUrl() {
        StringBuilder sb = new StringBuilder();

        sb.append(protocol).append("://").append(domain);

        if (port != null) {
            sb.append(':').append(port);
        }

        if (!Strings.isNullOrEmpty(path)) {
            if (!path.startsWith("/")) {
                sb.append("/");
            }
            sb.append(path);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return "Localization{" +
                "localizationId='" + localizationId + '\'' +
                ", domain='" + domain + '\'' +
                ", port='" + port + '\'' +
                ", path='" + path + '\'' +
                ", protocol='" + protocol + '\'' +
                ", locale='" + locale + '\'' +
                '}';
    }
}
