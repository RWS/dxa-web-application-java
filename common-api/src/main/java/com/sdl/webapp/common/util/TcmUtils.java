package com.sdl.webapp.common.util;

import java.util.StringTokenizer;

/**
 * TCM Utils
 *
 * Simple utility functions to process TCM-URIs.
 *
 * @author nic
 */
public final class TcmUtils {

    private TcmUtils() {}

    static public String buildPublicationTcmUri(int publicationId) {
        return "tcm:0-" + publicationId + "-1";
    }

    static public String buildTcmUri(int publicationId, int itemId, int itemType) {
        return "tcm:" + publicationId + "-" + itemId + "-" + itemType;
    }

    static public int getItemId(String tcmUri) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        if ( tokenizer.countTokens() > 2 ) {
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
        tokenizer.nextToken(); tokenizer.nextToken();
        while ( tokenizer.hasMoreTokens() ) {
            localizedTcmUri += "-" + tokenizer.nextToken();
        }
        return localizedTcmUri;
    }



}
