package com.sdl.webapp.common.api.model.sorting;

import com.google.common.collect.ComparisonChain;
import com.sdl.dxa.api.datamodel.model.SitemapItemModelData;
import com.sdl.webapp.common.api.model.entity.SitemapItem;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.comparator.NullSafeComparator;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * A private class that contains the results of the regex so they only have to be done once for a whole sorting.
 */
public class SortableSiteMap {
    private static final Pattern TAXONOMY_ID_PATTERN = Pattern.compile("^(\\w?)(\\d+)-(\\w?)(\\d+)$");

    public static final Comparator<SortableSiteMap> SORT_BY_TITLE_AND_ID = new NullSafeComparator<>((o1, o2) -> ComparisonChain.start()
            .compare(o1.getOriginalTitle(), o2.getOriginalTitle())
            .compare(o1.getId(), o2.getId())
            .result(), true);

    public static final Comparator<SortableSiteMap> SORT_BY_TAXONOMY_AND_KEYWORD = new NullSafeComparator<>((o1, o2) -> ComparisonChain.start()
            .compare(o1.getFirstChar(), o2.getFirstChar())
            .compare(o1.getFirstNumber(), o2.getFirstNumber())
            .compare(o1.getSecondChar(), o2.getSecondChar())
            .compare(o1.getSecondNumber(), o2.getSecondNumber())
            .result(), true);

    private Integer firstNumber = Integer.MIN_VALUE;
    private String firstChar = "";
    private Integer secondNumber = Integer.MIN_VALUE;
    private String secondChar = "";
    private String originalTitle = "";
    private String id = "";
    private SitemapItem sitemapItem;
    private SitemapItemModelData sitemapItemModelData;

    public SortableSiteMap(SitemapItemModelData sitemapItemModelData) {
        this.sitemapItemModelData = sitemapItemModelData;
        if (sitemapItemModelData == null) {
            return;
        }
        originalTitle = sitemapItemModelData.getOriginalTitle() == null ? "" : sitemapItemModelData.getOriginalTitle();
        id = sitemapItemModelData.getId() == null ? "" : sitemapItemModelData.getId();
        if (sitemapItemModelData.getId() == null) return;
        Matcher matcher = TAXONOMY_ID_PATTERN.matcher(sitemapItemModelData.getId());
        if (!matcher.matches()) {
            return;
        }
        fillFromMatcher(matcher);
    }

    public SortableSiteMap(SitemapItem sitemapItem) {
        this.sitemapItem = sitemapItem;
        if (sitemapItem == null) {
            return;
        }
        originalTitle = sitemapItem.getOriginalTitle() == null ? "" : sitemapItem.getOriginalTitle();
        id = sitemapItem.getId() == null ? "" : sitemapItem.getId();
        if (sitemapItem.getId() == null) return;
        Matcher matcher = TAXONOMY_ID_PATTERN.matcher(sitemapItem.getId());
        if (!matcher.matches()) {
            return;
        }
        fillFromMatcher(matcher);
    }

    private void fillFromMatcher(Matcher matcher) {
        String group2 = matcher.group(2);
        String group4 = matcher.group(4);
        if (StringUtils.isNotEmpty(group2)) {
            this.firstNumber = Integer.parseInt(group2);
        }
        if (StringUtils.isNotEmpty(group4)) {
            this.secondNumber = Integer.parseInt(group4);
        }
        this.firstChar = matcher.group(1);
        this.secondChar = matcher.group(3);
    }

    public Integer getFirstNumber() {
        return firstNumber;
    }

    public Integer getSecondNumber() {
        return secondNumber;
    }

    public String getFirstChar() {
        return firstChar;
    }

    public String getSecondChar() {
        return secondChar;
    }

    public SitemapItem getSitemapItem() {
        return sitemapItem;
    }

    public SitemapItemModelData getSitemapItemModelData() {
        return sitemapItemModelData;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getId() {
        return id;
    }

    public static Collection<SitemapItem> sortItem(Collection<SitemapItem> entries, Comparator<SortableSiteMap> comparator) {
        if (entries == null) return Collections.emptyList();
        return entries
                .stream()
                .map(SortableSiteMap::new)
                .sorted(comparator)
                .map(SortableSiteMap::getSitemapItem).collect(Collectors.toList());
    }

    public static Collection<SitemapItemModelData> sortModelData(Collection<SitemapItemModelData> entries, Comparator<SortableSiteMap> comparator) {
        if (entries == null) return Collections.emptyList();
        return entries
                .stream()
                .map(SortableSiteMap::new)
                .sorted(comparator)
                .map(SortableSiteMap::getSitemapItemModelData).collect(Collectors.toList());
    }

}
