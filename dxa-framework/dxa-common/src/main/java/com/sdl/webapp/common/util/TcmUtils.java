package com.sdl.webapp.common.util;

import com.sdl.dxa.api.datamodel.Constants;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Simple utility functions to process TCM-URIs.
 *
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

    private static final String DEFAULT_NAMESPACE = Constants.DEFAULT_NAMESPACE;

    private static final String NS_ID_ID = "%s:%s-%s";

    private static final String NS_ID_ID_ID = "%s:%s-%s-%s";

    private static final Pattern URI_SCHEMA = Pattern.compile("(?<namespace>tcm|ish):(?<publicationId>\\d+)-(?<itemId>\\d+)(-(?<itemType>\\d+))?");

    private TcmUtils() {
    }

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(int publicationId) {
        return buildPublicationTcmUri(DEFAULT_NAMESPACE, publicationId);
    }

    /**
     * Build a publication TCM URI looking like <code>NAMESPACE:0-ID-1</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(String namespace, int publicationId) {
        return buildPublicationTcmUriInternal(namespace, String.valueOf(publicationId));
    }

    /**
     * Build a publication TCM URI looking like <code>NAMESPACE:0-ID-1</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(String namespace, String publicationId) {
        return buildPublicationTcmUriInternal(namespace, publicationId);
    }

    private static String buildPublicationTcmUriInternal(String namespace, String publicationId) {
        return buildTcmUriInternalForRootPublication(namespace, publicationId, String.valueOf(PUBLICATION_ITEM_TYPE));
    }

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(String publicationId) {
        return buildPublicationTcmUriInternal(DEFAULT_NAMESPACE, publicationId);
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
        return buildTemplateTcmUri(DEFAULT_NAMESPACE, String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a template TCM URI looking like <code>NAMESPACE:PUB_ID-ITEM_ID-32</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for template
     */
    public static String buildTemplateTcmUri(String namespace, int publicationId, int itemId) {
        return buildTemplateTcmUri(namespace, String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a template TCM URI looking like <code>NAMESPACE:PUB_ID-ITEM_ID-32</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for template
     */
    public static String buildTemplateTcmUri(String namespace, String publicationId, String itemId) {
        return buildTcmUriInternal(namespace, publicationId, itemId, String.valueOf(COMPONENT_TEMPLATE_ITEM_TYPE));
    }

    /**
     * Build a page TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for page
     */
    public static String buildPageTcmUri(Object publicationId, Object itemId) {
        return buildTcmUri(publicationId, itemId, PAGE_ITEM_TYPE);
    }

    /**
     * Build a page TCM URI looking like <code>NAMESPACE:PUB_ID-ITEM_ID-32</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for page
     */
    public static String buildPageTcmUri(String namespace, Object publicationId, Object itemId) {
        return buildTcmUri(namespace, publicationId, itemId, PAGE_ITEM_TYPE);
    }

    /**
     * See {@link #buildKeywordTcmUri(int, int)}.
     */
    public static String buildKeywordTcmUri(int publicationId, int itemId) {
        return buildKeywordTcmUri(String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * See {@link #buildKeywordTcmUri(String, String, String)}.
     */
    public static String buildKeywordTcmUri(String namespace, int publicationId, int itemId) {
        return buildKeywordTcmUri(namespace, String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-1024</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for keyword
     */
    public static String buildKeywordTcmUri(String publicationId, String itemId) {
        return buildTcmUri(publicationId, itemId, KEYWORD_ITEM_TYPE);
    }

    /**
     * Build a template TCM URI looking like <code>NAMESPACE:PUB_ID-ITEM_ID-1024</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for keyword
     */
    public static String buildKeywordTcmUri(String namespace, String publicationId, String itemId) {
        return buildTcmUri(namespace, publicationId, itemId, KEYWORD_ITEM_TYPE);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(String publicationId, String itemId) {
        return buildTcmUriInternal(DEFAULT_NAMESPACE, publicationId, itemId);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(Object publicationId, Object itemId) {
        return buildTcmUriInternal(DEFAULT_NAMESPACE, String.valueOf(publicationId), String.valueOf(itemId));
    }

    /**
     * Build a short URI looking like <code>NAMESPACE:PUB_ID-ITEM_ID</code>. For example ish:10054-64587 or tcm:16-345
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(String namespace, int publicationId, int itemId) {
        return buildTcmUriInternal(namespace, String.valueOf(publicationId), String.valueOf(itemId));
    }

    private static String buildTcmUriInternal(String namespace, String publicationId, String itemId) {
        return String.format(NS_ID_ID, namespace, publicationId, itemId);
    }

    /**
     * Build a TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-ITEM_TYPE</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @param itemType      Type of the item ({see the @link com.tridion.ItemTypes})
     * @return a TCM URI
     */
    public static String buildTcmUri(String publicationId, String itemId, String itemType) {
        return buildTcmUriInternal(DEFAULT_NAMESPACE, publicationId, itemId, itemType);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-ITEM_TYPE</code>. For example ish:10054-64587-8 or tcm:16-34521-1
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @param itemType      Type of the item ({see the @link com.tridion.ItemTypes})
     * @return short TCM URI
     */
    public static String buildTcmUri(Object publicationId, Object itemId, int itemType) {
        return buildTcmUriInternal(DEFAULT_NAMESPACE, String.valueOf(publicationId), String.valueOf(itemId), String.valueOf(itemType));
    }

    /**
     * Build a short TCM URI looking like <code>NAMESPACE:PUB_ID-ITEM_ID</code>.
     *
     * @param namespace     Custom namespace. At this moment only [tcm, ish] are supported
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(String namespace, Object publicationId, Object itemId, Object itemType) {
        return buildTcmUriInternal(namespace, String.valueOf(publicationId), String.valueOf(itemId), String.valueOf(itemType));
    }

    private static String buildTcmUriInternal(String namespace, String publicationId, String itemId, String itemType) {
        return String.format(NS_ID_ID_ID, namespace, publicationId, itemId, itemType);
    }

    private static String buildTcmUriInternalForRootPublication(String namespace, String publicationId, String itemType) {
        return String.format(NS_ID_ID_ID, namespace, "0", publicationId, itemType);
    }

    /**
     * Extracts item type from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return item type ID or <code>-1</code> if URI is not valid or null
     */
    public static int getItemType(String tcmUri) {
        ExtractResult extractResult = extractGroupFromTcm(tcmUri, "itemType");
        return extractResult.status == ExtractStatus.NO_MATCH ? COMPONENT_ITEM_TYPE : extractResult.getInt();
    }

    private static ExtractResult extractGroupFromTcm(String tcmUri, String group) {
        if (tcmUri == null) {
            return new ExtractResult(ExtractStatus.FAILED, null);
        }

        Matcher matcher = URI_SCHEMA.matcher(tcmUri);
        if (matcher.matches()) {
            String match = matcher.group(group);
            return match == null ?
                    new ExtractResult(ExtractStatus.NO_MATCH, null) :
                    new ExtractResult(ExtractStatus.SUCCESS, match);
        }
        return new ExtractResult(ExtractStatus.FAILED, null);
    }

    /**
     * Extracts publication ID from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return publication ID or <code>-1</code> if URI is not valid or null
     */
    public static int getPublicationId(String tcmUri) {
        return extractGroupFromTcm(tcmUri, "publicationId").getInt();
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
        return extractGroupFromTcm(tcmUri, "itemId").getInt();
    }

    /**
     * Extracts namespace from a valid TCM URI.
     *
     * @param tcmUri tcm uri to process
     * @return namespace or <code>-1</code> if URI is not valid or null
     */
    public static String getNamespace(String tcmUri) {
        ExtractResult extractResult = extractGroupFromTcm(tcmUri, "namespace");
        return extractResult.status == ExtractStatus.SUCCESS ? extractResult.result : null;
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
        Matcher matcher = URI_SCHEMA.matcher(tcmUri);
        if (!matcher.matches()) {
            log.warn("TCM URI {} is not valid", tcmUri);
            throw new IllegalArgumentException("TCM URI is not valid: " + tcmUri);
        }
        String itemType = matcher.group("itemType");
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
        return buildTcmUriInternal(DEFAULT_NAMESPACE, String.valueOf(publicationId), String.valueOf(itemId));
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
        return buildTcmUriInternal(DEFAULT_NAMESPACE, String.valueOf(publicationId), String.valueOf(itemId), String.valueOf(itemType));
    }

    /**
     * Returns if the passed string is TMC URI.
     *
     * @param tcmUri object to check
     * @return whether the string is TCM URI
     */
    public static boolean isTcmUri(@Nullable Object tcmUri) {
        return tcmUri != null && URI_SCHEMA.matcher(String.valueOf(tcmUri)).matches();
    }

    private enum ExtractStatus {
        FAILED, NO_MATCH, SUCCESS
    }

    @Value
    private static class ExtractResult {

        private ExtractStatus status;

        private String result;

        public int getInt() {
            return status != ExtractStatus.SUCCESS || result == null ? -1 : Integer.parseInt(result);
        }
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
