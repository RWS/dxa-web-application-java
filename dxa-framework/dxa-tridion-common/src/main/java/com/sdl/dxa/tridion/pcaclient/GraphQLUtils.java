package com.sdl.dxa.tridion.pcaclient;

import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;

public class GraphQLUtils {
    public static ContentNamespace convertUriToGraphQLContentNamespace(String cmUriScheme) {
        switch (cmUriScheme) {
            case "ish":
                return ContentNamespace.Docs;
            case "tcm":
                return ContentNamespace.Sites;
            default:
                return ContentNamespace.Sites;
        }
    }
}
