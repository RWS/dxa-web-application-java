package com.sdl.tridion.referenceimpl.common.config;

import com.google.common.base.Strings;

public final class Localization {

    public static final class Builder {
        private int localizationId;
        private String protocol;
        private String domain;
        private String port;
        private String path;

        private Builder() {
        }

        public Builder setLocalizationId(int localizationId) {
            this.localizationId = localizationId;
            return this;
        }

        public Builder setProtocol(String protocol) {
            this.protocol = protocol;
            return this;
        }

        public Builder setDomain(String domain) {
            this.domain = domain;
            return this;
        }

        public Builder setPort(String port) {
            this.port = port;
            return this;
        }

        public Builder setPath(String path) {
            this.path = path;
            return this;
        }

        public Localization build() {
            return new Localization(this);
        }
    }

    private final int localizationId;
    private final String protocol;
    private final String domain;
    private final String port;
    private final String path;

    private Localization(Builder builder) {
        this.localizationId = builder.localizationId;
        this.protocol = builder.protocol;
        this.domain = builder.domain;
        this.port = builder.port;
        this.path = builder.path;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public int getLocalizationId() {
        return localizationId;
    }

    public String getProtocol() {
        return protocol;
    }

    public String getDomain() {
        return domain;
    }

    public String getPort() {
        return port;
    }

    public String getPath() {
        return path;
    }

    public String getBaseUrl() {
        StringBuilder sb = new StringBuilder();

        sb.append(protocol).append("://").append(domain);

        if (!Strings.isNullOrEmpty(port)) {
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
                "localizationId=" + localizationId +
                ", protocol='" + protocol + '\'' +
                ", domain='" + domain + '\'' +
                ", port='" + port + '\'' +
                ", path='" + path + '\'' +
                '}';
    }
}
