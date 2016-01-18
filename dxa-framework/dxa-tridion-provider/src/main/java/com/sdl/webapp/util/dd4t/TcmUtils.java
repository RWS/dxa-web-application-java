package com.sdl.webapp.util.dd4t;

import java.util.StringTokenizer;

/**
 * Simple utility functions to process TCM-URIs.
 */
public final class TcmUtils {

    public static final int TEMPLATE_ITEM_TYPE = 32;

    private static final String TCM_S_S = "tcm:%s-%s";
    private static final String TCM_S_S_S = "tcm:%s-%s-%s";

    private TcmUtils() {
    }

    static public String buildPublicationTcmUri(int publicationId) {
        return String.format(TCM_S_S_S, 0, publicationId, 1);
    }

    static public String buildTemplateTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, TEMPLATE_ITEM_TYPE);
    }

    static public String buildTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S, publicationId, itemId);
    }

    static public String buildTcmUri(int publicationId, int itemId) {
        return String.format(TCM_S_S, publicationId, itemId);
    }

    static public String buildTcmUri(String publicationId, String itemId, String itemType) {
        return String.format(TCM_S_S_S, publicationId, itemId, itemType);
    }

    static public String buildTcmUri(int publicationId, int itemId, int itemType) {
        return String.format(TCM_S_S_S, publicationId, itemId, itemType);
    }

    static public int getItemId(String tcmUri) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        if (tokenizer.countTokens() > 2) {
            tokenizer.nextToken();
            tokenizer.nextToken();
            return Integer.parseInt(tokenizer.nextToken());
        }
        return -1;
    }

    static public int getPublicationId(String tcmUri) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        tokenizer.nextToken();
        return Integer.parseInt(tokenizer.nextToken());
    }

    static public String localizeTcmUri(String tcmUri, String publicationTcmUri) {
        int publicationId = getItemId(publicationTcmUri);
        return localizeTcmUri(tcmUri, publicationId);
    }

    static public String localizeTcmUri(String tcmUri, int publicationId) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        String localizedTcmUri = "tcm:" + publicationId;
        tokenizer.nextToken();
        tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            localizedTcmUri += "-" + tokenizer.nextToken();
        }
        return localizedTcmUri;
    }


}
