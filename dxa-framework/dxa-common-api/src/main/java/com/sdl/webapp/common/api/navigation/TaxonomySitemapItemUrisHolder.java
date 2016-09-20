package com.sdl.webapp.common.api.navigation;

import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.util.TcmUtils;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * Wrapper that holds information about taxonomy, keyword, page URIs.
 */
@Slf4j
@EqualsAndHashCode
@ToString
public class TaxonomySitemapItemUrisHolder {

    private static final Pattern PATTERN = Pattern.compile("t(?<taxonomyId>\\d+)(-(?<type>[kp])(?<itemId>\\d+))?$");

    private final boolean page;

    private final String taxonomyId;

    private final String pageOrKeywordId;

    private final String localizationId;

    @Getter(lazy = true)
    private final String taxonomyUri = taxonomyUri();

    @Getter(lazy = true)
    private final String keywordUri = keywordUri();

    @Getter(lazy = true)
    private final String pageUri = pageUri();

    private TaxonomySitemapItemUrisHolder(String taxonomyId, String pageOrKeywordId, boolean page, String localizationId) {
        this.page = page;
        this.taxonomyId = taxonomyId;
        this.pageOrKeywordId = pageOrKeywordId;
        this.localizationId = localizationId;
    }

    /**
     * Parse a sitemapItem ID and returns a wrapper that can return URIs for all parts of id.
     *
     * @param sitemapItemId id to parse
     * @param localization  context localization
     * @return an URIs wrapper or null if ID format is wrong
     */
    @Nullable
    public static TaxonomySitemapItemUrisHolder parse(String sitemapItemId, @NonNull Localization localization) {
        log.trace("Parsing sitemapItemId {} for localization {}", sitemapItemId, localization.getId());

        if (isNullOrEmpty(sitemapItemId)) {
            return null;
        }
        Matcher matcher = PATTERN.matcher(sitemapItemId);

        return !matcher.matches() ? null :
                new TaxonomySitemapItemUrisHolder(matcher.group("taxonomyId"), matcher.group("itemId"), "p".equals(matcher.group("type")), localization.getId());
    }

    public boolean isKeyword() {
        return pageOrKeywordId != null && !page;
    }

    public boolean isPage() {
        return pageOrKeywordId != null && page;
    }

    public boolean isTaxonomyOnly() {
        return pageOrKeywordId == null;
    }

    private String taxonomyUri() {
        return TcmUtils.buildTcmUri(localizationId, taxonomyId, "512");
    }

    private String keywordUri() {
        return getRightIf(isKeyword(), "1024");
    }

    private String pageUri() {
        return getRightIf(isPage(), "64");
    }

    private String getRightIf(boolean returnValue, String typeId) {
        return returnValue ? TcmUtils.buildTcmUri(localizationId, pageOrKeywordId, typeId) : null;
    }
}
