package com.sdl.webapp.common.util;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utility functions to process TCM-URIs.
 * @dxa.publicApi
 */
@Slf4j
public final class TcmUtils {

    public static final int PUBLICATION_ITEM_TYPE = 1;

    public static final int FOLDER_ITEM_TYPE = 2;

    public static final int STRUCTURE_GROUP_ITEM_TYPE = 4;

    public static final int SCHEMA_ITEM_TYPE = 8;

    public static final int COMPONENT_ITEM_TYPE = 16;

    public static final int COMPONENT_TEMPLATE_ITEM_TYPE = 32;

    public static final int PAGE_ITEM_TYPE = 64;

    public static final int PAGE_TEMPLATE_ITEM_TYPE = 128;

    public static final int TARGET_GROUP_ITEM_TYPE = 256;

    public static final int CATEGORY_ITEM_TYPE = 512;

    public static final int KEYWORD_ITEM_TYPE = 1024;

    private static final String TCM_S_S = "tcm:%s-%s";

    private static final String TCM_S_S_S = "tcm:%s-%s-%s";

    private static final Pattern PATTERN = Pattern.compile("tcm:(?<publicationId>\\d+)-(?<itemId>\\d+)(-(?<itemType>\\d+))?");

    private TcmUtils() {
    }

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(int publicationId) {
        return buildPublicationTcmUriInternal(String.valueOf(publicationId));
    }

    private static String buildPublicationTcmUriInternal(String publicationId) {
        return String.format(TCM_S_S_S, 0, publicationId, PUBLICATION_ITEM_TYPE);
    }

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(String publicationId) {
        return buildPublicationTcmUriInternal(publicationId);
    }

    /**
     * See {@link #buildTemplateTcmUri(String, String)}.
     */
    public static String buildTemplateTcmUri(int publicationId, int itemId) {
        return buildTemplateTcmUri(String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for template
     */
    public static String buildTemplateTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, COMPONENT_TEMPLATE_ITEM_TYPE);
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for page
     */
    public static String buildPageTcmUri(Object publicationId, Object itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, PAGE_ITEM_TYPE);
    }

    /**
     * See {@link #buildKeywordTcmUri(String, String)}.
     */
    public static String buildKeywordTcmUri(int publicationId, int itemId) {
        return buildKeywordTcmUri(String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-1024</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for keyword
     */
    public static String buildKeywordTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, KEYWORD_ITEM_TYPE);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(String publicationId, String itemId) {
        return buildTcmUriInternal(publicationId, itemId);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(Object publicationId, Object itemId) {
        return buildTcmUriInternal(String.valueOf(publicationId), String.valueOf(itemId));
    }

    private static String buildTcmUriInternal(String publicationId, String itemId) {
        return String.format(TCM_S_S, publicationId, itemId);
    }

    /**
     * Build a TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-ITEM_TYPE</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @param itemType      item type
     * @return a TCM URI
     */
    public static String buildTcmUri(String publicationId, String itemId, String itemType) {
        return buildTcmUriInternal(publicationId, itemId, itemType);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(Object publicationId, Object itemId, int itemType) {
        return buildTcmUriInternal(String.valueOf(publicationId), String.valueOf(itemId), String.valueOf(itemType));
    }


    public static String buildTcmUriInternal(String publicationId, String itemId, String itemType) {
        return String.format(TCM_S_S_S, publicationId, itemId, itemType);
    }

    /**
     * Extracts item type from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return item type ID or <code>-1</code> if URI is not valid or null
     */
    public static int getItemType(String tcmUri) {
        int itemType = extractGroupFromTcm(tcmUri, "itemType");
        return itemType == -2 ? COMPONENT_ITEM_TYPE : itemType;
    }

    private static int extractGroupFromTcm(String tcmUri, String group) {
        int failed = -1;
        if (tcmUri == null) {
            return failed;
        }

        Matcher matcher = PATTERN.matcher(tcmUri);
        if (matcher.matches()) {
            String match = matcher.group(group);
            return match != null ? Integer.parseInt(match) : -2;
        }
        return failed;
    }

    /**
     * Extracts publication ID from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return publication ID or <code>-1</code> if URI is not valid or null
     */
    public static int getPublicationId(String tcmUri) {
        return extractGroupFromTcm(tcmUri, "publicationId");
    }

    /**
     * Localizes given TCM URI to current publication.
     * <p>E.g. <code>tcm:1-2-3</code> with publication URI <code>tcm:0-8-1</code> will look like <code>tcm:8-2-3</code>.</p>
     *
     * @param tcmUri            tcm uri of item to localize
     * @param publicationTcmUri TCM URI of publication
     * @return localized TCM URI of an item
     */
    public static String localizeTcmUri(String tcmUri, String publicationTcmUri) {
        int publicationId = getItemId(publicationTcmUri);
        return localizeTcmUri(tcmUri, publicationId);
    }

    /**
     * Extracts item ID from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return item ID or <code>-1</code> if URI is not valid or null
     */
    public static int getItemId(String tcmUri) {

        return extractGroupFromTcm(tcmUri, "itemId");
    }

    /**
     * Localizes given TCM URI to current publication.
     * <p>E.g. <code>tcm:1-2-3</code> with publication ID <code>8</code> will look like <code>tcm:8-2-3</code>.</p>
     *
     * @param tcmUri        tcm uri of item to localize
     * @param publicationId publication ID
     * @return localized TCM URI of an item
     */
    public static String localizeTcmUri(String tcmUri, int publicationId) {
        Matcher matcher = PATTERN.matcher(tcmUri);
        if (!matcher.matches()) {
            log.warn("TCM URI {} is not valid", tcmUri);
            throw new IllegalArgumentException("TCM URI is not valid: " + tcmUri);
        }
        String itemType = matcher.group(4);
        return itemType == null ? buildTcmUri(publicationId, getItemId(tcmUri)) :
                buildTcmUri(publicationId, getItemId(tcmUri), Integer.parseInt(itemType));

    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(int publicationId, int itemId) {
        return buildTcmUriInternal(String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-ITEM_TYPE</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @param itemType      item type
     * @return a TCM URI
     */
    public static String buildTcmUri(int publicationId, int itemId, int itemType) {
        return buildTcmUriInternal(String.valueOf(publicationId), String.valueOf(itemId), String.valueOf(itemType));
    }

    /**
     * Returns if the passed string is TMC URI.
     *
     * @param tcmUri object to check
     * @return whether the string is TCM URI
     */
    public static boolean isTcmUri(@Nullable Object tcmUri) {
        return tcmUri != null && PATTERN.matcher(String.valueOf(tcmUri)).matches();
    }

    /**
     * Utils for Taxonomies identifiers support.
     */
    public static final class Taxonomies {

        private Taxonomies() {
        }

        public static String getTaxonomySitemapIdentifier(String taxonomyId) {
            return String.format("t%s", taxonomyId);
        }

        public static String getTaxonomySitemapIdentifier(String taxonomyId, SitemapItemType type, String subItemId) {
            return String.format("t%s-%s%s", taxonomyId, type == SitemapItemType.PAGE ? "p" : "k", subItemId);
        }

        /**
         * Type of item for taxonomy-based navigation.
         */
        public enum SitemapItemType {
            PAGE, KEYWORD
        }
    }
}
