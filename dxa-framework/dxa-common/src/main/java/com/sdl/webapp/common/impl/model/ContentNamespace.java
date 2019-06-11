package com.sdl.webapp.common.impl.model;

public enum ContentNamespace {
    Docs("ish"),
    Sites("tcm");

    private String namespace;

    ContentNamespace(String namespace) {
        this.namespace = namespace;
    }

    public static ContentNamespace getByUriType(String uriType) {
        for (ContentNamespace value : values()) {
            if (value.namespace.equals(uriType)) return value;
        }
        return Sites;
    }

    public String nameSpace() {
        return namespace;
    }
}
