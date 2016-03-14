package com.sdl.webapp.util.dd4t;

import java.util.StringTokenizer;

/**
 * Simple utility functions to process TCM-URIs.
 */
public final class TcmUtils {

    /**
     * Constant <code>PUBLICATION_ITEM_TYPE=1</code>
     */
    public static final int PUBLICATION_ITEM_TYPE = 1;
    /**
     * Constant <code>TEMPLATE_ITEM_TYPE=32</code>
     */
    public static final int TEMPLATE_ITEM_TYPE = 32;
    /** Constant <code>PAGE_ITEM_TYPE=64</code> */
    public static final int PAGE_ITEM_TYPE = 64;

    private static final String TCM_S_S = "tcm:%s-%s";
    private static final String TCM_S_S_S = "tcm:%s-%s-%s";

    private TcmUtils() {
    }

    /**
     * <p>buildPublicationTcmUri.</p>
     *
     * @param publicationId a int.
     * @return a {@link java.lang.String} object.
     */
    static public String buildPublicationTcmUri(int publicationId) {
        return String.format(TCM_S_S_S, 0, publicationId, PUBLICATION_ITEM_TYPE);
    }

    static public String buildPublicationTcmUri(String publicationId) {
        return String.format(TCM_S_S_S, 0, publicationId, PUBLICATION_ITEM_TYPE);
    }

    /**
     * <p>buildTemplateTcmUri.</p>
     *
     * @param publicationId a {@link java.lang.String} object.
     * @param itemId a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String buildTemplateTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, TEMPLATE_ITEM_TYPE);
    }

    /**
     * <p>buildPageTcmUri.</p>
     *
     * @param publicationId a {@link java.lang.String} object.
     * @param itemId a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String buildPageTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, PAGE_ITEM_TYPE);
    }

    /**
     * <p>buildTcmUri.</p>
     *
     * @param publicationId a {@link java.lang.String} object.
     * @param itemId a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String buildTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S, publicationId, itemId);
    }

    /**
     * <p>buildTcmUri.</p>
     *
     * @param publicationId a int.
     * @param itemId a int.
     * @return a {@link java.lang.String} object.
     */
    static public String buildTcmUri(int publicationId, int itemId) {
        return String.format(TCM_S_S, publicationId, itemId);
    }

    /**
     * <p>buildTcmUri.</p>
     *
     * @param publicationId a {@link java.lang.String} object.
     * @param itemId a {@link java.lang.String} object.
     * @param itemType a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String buildTcmUri(String publicationId, String itemId, String itemType) {
        return String.format(TCM_S_S_S, publicationId, itemId, itemType);
    }

    /**
     * <p>buildTcmUri.</p>
     *
     * @param publicationId a int.
     * @param itemId a int.
     * @param itemType a int.
     * @return a {@link java.lang.String} object.
     */
    static public String buildTcmUri(int publicationId, int itemId, int itemType) {
        return String.format(TCM_S_S_S, publicationId, itemId, itemType);
    }

    /**
     * <p>getItemId.</p>
     *
     * @param tcmUri a {@link java.lang.String} object.
     * @return a int.
     */
    static public int getItemId(String tcmUri) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        if (tokenizer.countTokens() > 2) {
            tokenizer.nextToken();
            tokenizer.nextToken();
            return Integer.parseInt(tokenizer.nextToken());
        }
        return -1;
    }

    /**
     * <p>getPublicationId.</p>
     *
     * @param tcmUri a {@link java.lang.String} object.
     * @return a int.
     */
    static public int getPublicationId(String tcmUri) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        tokenizer.nextToken();
        return Integer.parseInt(tokenizer.nextToken());
    }

    /**
     * <p>localizeTcmUri.</p>
     *
     * @param tcmUri a {@link java.lang.String} object.
     * @param publicationTcmUri a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    static public String localizeTcmUri(String tcmUri, String publicationTcmUri) {
        int publicationId = getItemId(publicationTcmUri);
        return localizeTcmUri(tcmUri, publicationId);
    }

    /**
     * <p>localizeTcmUri.</p>
     *
     * @param tcmUri a {@link java.lang.String} object.
     * @param publicationId a int.
     * @return a {@link java.lang.String} object.
     */
    static public String localizeTcmUri(String tcmUri, int publicationId) {
        StringTokenizer tokenizer = new StringTokenizer(tcmUri, ":-");
        StringBuilder localizedTcmUri = new StringBuilder("tcm:").append(publicationId);
        tokenizer.nextToken();
        tokenizer.nextToken();
        while (tokenizer.hasMoreTokens()) {
            localizedTcmUri.append('-').append(tokenizer.nextToken());
        }
        return localizedTcmUri.toString();
    }


}
