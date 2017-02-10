package com.sdl.webapp.common.util;

import com.sdl.webapp.common.api.model.entity.SitemapItem;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.sdl.webapp.common.util.TcmUtils.Namespace.TCM;

/**
 * Simple utility functions to process TCM-URIs.
 */
@Slf4j
public final class TcmUtils {

    private static final int PUBLICATION_ITEM_TYPE = 1;

    private static final int TEMPLATE_ITEM_TYPE = 32;

    private static final int PAGE_ITEM_TYPE = 64;

    private static final String S_S_S = "%s:%s-%s";

    private static final String S_S_S_S = "%s:%s-%s-%s";

    private static final Pattern PATTERN = Pattern.compile("(\\w+):(\\d+)-(\\d+)(-(\\d+))?");

    private TcmUtils() {
    }

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(int publicationId) {
        return buildPublicationCmUri(TCM, publicationId);
    }

    /**
     * Build a publication CM URI looking like <code>namespace:0-ID-1</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationCmUri(Namespace namespace, int publicationId) {
        return buildPublicationCmUriInternal(namespace, String.valueOf(publicationId));
    }

    private static String buildPublicationCmUriInternal(Namespace namespace, String publicationId) {
        return String.format(S_S_S_S, namespace.getValue(), 0, publicationId, PUBLICATION_ITEM_TYPE);
    }

    /**
     * Build a publication TCM URI looking like <code>tcm:0-ID-1</code>.
     *
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationTcmUri(String publicationId) {
        return buildPublicationCmUri(TCM, publicationId);
    }

    /**
     * Build a publication CM URI looking like <code>namespace:0-ID-1</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @return TCM URI for publication
     */
    public static String buildPublicationCmUri(Namespace namespace, String publicationId) {
        return buildPublicationCmUriInternal(namespace, publicationId);
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for template
     */
    public static String buildTemplateTcmUri(String publicationId, String itemId) {
        return buildTemplateCmUri(TCM, publicationId, itemId);
    }

    /**
     * Build a template CM URI looking like <code>namespace:PUB_ID-ITEM_ID-32</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for template
     */
    public static String buildTemplateCmUri(Namespace namespace, String publicationId, String itemId) {
        return String.format(S_S_S_S, namespace.getValue(), publicationId, itemId, TEMPLATE_ITEM_TYPE);
    }

    /**
     * Build a template TCM URI looking like <code>tcm:PUB_ID-ITEM_ID-32</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for page
     */
    public static String buildPageTcmUri(String publicationId, String itemId) {
        return buildPageCmUri(TCM, publicationId, itemId);
    }

    /**
     * Build a template CM URI looking like <code>namespace:PUB_ID-ITEM_ID-32</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return TCM URI for page
     */
    public static String buildPageCmUri(Namespace namespace, String publicationId, String itemId) {
        return String.format(S_S_S_S, namespace.getValue(), publicationId, itemId, PAGE_ITEM_TYPE);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(String publicationId, String itemId) {
        return buildCmUri(TCM, publicationId, itemId);
    }

    /**
     * Build a short CM URI looking like <code>namespace:PUB_ID-ITEM_ID</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short CM URI
     */
    public static String buildCmUri(Namespace namespace, String publicationId, String itemId) {
        return buildCmUriInternal(namespace, publicationId, itemId);
    }

    private static String buildCmUriInternal(Namespace namespace, String publicationId, String itemId) {
        return String.format(S_S_S, namespace.getValue(), publicationId, itemId);
    }

    /**
     * Build a short TCM URI looking like <code>tcm:PUB_ID-ITEM_ID</code>.
     *
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short TCM URI
     */
    public static String buildTcmUri(int publicationId, int itemId) {
        return buildCmUri(TCM, publicationId, itemId);
    }

    /**
     * Build a short CM URI looking like <code>namespace:PUB_ID-ITEM_ID</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @param itemId        item ID
     * @return short CM URI
     */
    public static String buildCmUri(Namespace namespace, int publicationId, int itemId) {
        return buildCmUriInternal(namespace, String.valueOf(publicationId), String.valueOf(itemId));
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
        return buildCmUri(TCM, publicationId, itemId, itemType);
    }

    /**
     * Build a CM URI looking like <code>namespace:PUB_ID-ITEM_ID-ITEM_TYPE</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @param itemId        item ID
     * @param itemType      item type
     * @return a CM URI
     */
    public static String buildCmUri(Namespace namespace, String publicationId, String itemId, String itemType) {
        return buildCmUriInternal(namespace, publicationId, itemId, itemType);
    }

    public static String buildCmUriInternal(Namespace namespace, String publicationId, String itemId, String itemType) {
        return String.format(S_S_S_S, namespace.getValue(), publicationId, itemId, itemType);
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
        return buildCmUri(TCM, publicationId, itemId, itemType);
    }

    /**
     * Build a CM URI looking like <code>namespace:PUB_ID-ITEM_ID-ITEM_TYPE</code>.
     *
     * @param namespace     CM URI namespace
     * @param publicationId publication ID
     * @param itemId        item ID
     * @param itemType      item type
     * @return a CM URI
     */
    public static String buildCmUri(Namespace namespace, int publicationId, int itemId, int itemType) {
        return buildCmUriInternal(namespace, String.valueOf(publicationId), String.valueOf(itemId), String.valueOf(itemType));
    }

    /**
     * Extracts publication ID from a valid CM URI.
     *
     * @param cmUri cm uri to process
     * @return publication ID or <code>-1</code> if URI is not valid or null
     */
    public static int getPublicationId(String cmUri) {
        return extractGroupFromCm(cmUri, 2);
    }

    private static int extractGroupFromCm(String cmUri, int group) {
        int failed = -1;
        if (cmUri == null) {
            return failed;
        }

        Matcher matcher = PATTERN.matcher(cmUri);
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
        return localizeCmUri(TCM, tcmUri, publicationTcmUri);
    }

    /**
     * Localizes given CM URI to current publication.
     * <p>E.g. <code>namespace:1-2-3</code> with publication URI <code>namespace:0-8-1</code> will look like <code>namespace:8-2-3</code>.</p>
     *
     * @param namespace        CM URI namespace
     * @param cmUri            cm uri of item to localize
     * @param publicationCmUri CM URI of publication
     * @return localized CM URI of an item
     */
    public static String localizeCmUri(Namespace namespace, String cmUri, String publicationCmUri) {
        int publicationId = getItemId(publicationCmUri);
        return localizeCmUri(namespace, cmUri, publicationId);
    }

    /**
     * Extracts item ID from a valid CM URI.
     *
     * @param cmUri cm uri to process
     * @return item ID or <code>-1</code> if URI is not valid or null
     */
    public static int getItemId(String cmUri) {
        return extractGroupFromCm(cmUri, 3);
    }

    /**
     * Localizes given CM URI to current publication.
     * <p>E.g. <code>namespace:1-2-3</code> with publication ID <code>8</code> will look like <code>namespace:8-2-3</code>.</p>
     *
     * @param namespace     CM URI namespace
     * @param cmUri         cm uri of item to localize
     * @param publicationId publication ID
     * @return localized CM URI of an item
     */
    public static String localizeCmUri(Namespace namespace, String cmUri, int publicationId) {
        Matcher matcher = PATTERN.matcher(cmUri);
        if (!matcher.matches()) {
            log.warn("CM URI {} is not valid", cmUri);
            throw new IllegalArgumentException("CM URI is not valid: " + cmUri);
        }
        String itemType = matcher.group(5);
        return itemType == null ? buildCmUri(namespace, publicationId, getItemId(cmUri)) :
                buildCmUri(namespace, publicationId, getItemId(cmUri), Integer.parseInt(itemType));

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
        return localizeCmUri(TCM, tcmUri, publicationId);
    }

    @AllArgsConstructor
    public enum Namespace {
        TCM("tcm"),
        ISH("ish");

        private static final Map<String, Namespace> namespaces = Collections.unmodifiableMap(initialize());

        @Getter
        private String value;

        private static Map<String, Namespace> initialize() {
            Map<String, Namespace> result = new HashMap<>();
            for (Namespace n : Namespace.values()) {
                result.put(n.value, n);
            }
            return result;
        }

        public static Namespace getNamespaceFor(String value) {
            return namespaces.get(value);
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
         * Type of a {@link SitemapItem} for taxonomy-based navigation.
         */
        public enum SitemapItemType {
            PAGE, KEYWORD
        }
    }
}
