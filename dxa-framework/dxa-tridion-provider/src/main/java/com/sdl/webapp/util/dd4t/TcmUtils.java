package com.sdl.webapp.util.dd4t;

import com.sdl.webapp.common.api.model.entity.SitemapItem;
import lombok.extern.slf4j.Slf4j;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utility functions to process TCM-URIs.
 */
@Slf4j
public final class TcmUtils {

    private static final int PUBLICATION_ITEM_TYPE = 1;

    private static final int TEMPLATE_ITEM_TYPE = 32;

    private static final int PAGE_ITEM_TYPE = 64;

    private static final String TCM_S_S = "tcm:%s-%s";

    private static final String TCM_S_S_S = "tcm:%s-%s-%s";

    private static final Pattern PATTERN = Pattern.compile("tcm:(\\d+)-(\\d+)(-(\\d+))?");

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

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(String publicationId) {
        return buildPublicationTcmUriInternal(publicationId);
    }

    private static String buildPublicationTcmUriInternal(String publicationId) {
        return String.format(TCM_S_S_S, 0, publicationId, PUBLICATION_ITEM_TYPE);
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for template
     */
    public static String buildTemplateTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, TEMPLATE_ITEM_TYPE);
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for page
     */
    public static String buildPageTcmUri(String publicationId, String itemId) {
        return String.format(TCM_S_S_S, publicationId, itemId, PAGE_ITEM_TYPE);
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
    public static String buildTcmUri(int publicationId, int itemId) {
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

    public static String buildTcmUriInternal(String publicationId, String itemId, String itemType) {
        return String.format(TCM_S_S_S, publicationId, itemId, itemType);
    }

    /**
     * Extracts item ID from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return item ID or <code>-1</code> if URI is not valid or null
     */
    public static int getItemId(String tcmUri) {
        return extractGroupFromTcm(tcmUri, 2);
    }

    /**
     * Extracts publication ID from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return publication ID or <code>-1</code> if URI is not valid or null
     */
    public static int getPublicationId(String tcmUri) {
        return extractGroupFromTcm(tcmUri, 1);
    }

    private static int extractGroupFromTcm(String tcmUri, int group) {
        int failed = -1;
        if (tcmUri == null) {
            return failed;
        }

        Matcher matcher = PATTERN.matcher(tcmUri);
        return matcher.matches() ? Integer.parseInt(matcher.group(group)) : failed;
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
         * Type of a {@link SitemapItem} for taxonomy-based navigation.
         */
        public enum SitemapItemType {
            PAGE, KEYWORD
        }
    }
}
