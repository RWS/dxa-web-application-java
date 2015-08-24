package com.sdl.webapp.tridion.query;

import com.google.common.base.Strings;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.sdl.webapp.common.api.model.entity.Link;
import com.sdl.webapp.common.api.model.entity.Teaser;
import com.tridion.broker.StorageException;
import com.tridion.broker.querying.MetadataType;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.criteria.content.PublicationCriteria;
import com.tridion.broker.querying.criteria.operators.AndCriteria;
import com.tridion.broker.querying.criteria.taxonomy.TaxonomyKeywordCriteria;
import com.tridion.broker.querying.filter.LimitFilter;
import com.tridion.broker.querying.filter.PagingFilter;
import com.tridion.broker.querying.sorting.SortColumn;
import com.tridion.broker.querying.sorting.SortDirection;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.broker.querying.sorting.column.CustomMetaKeyColumn;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.ComponentMetaFactory;
import com.tridion.meta.CustomMeta;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BrokerQuery {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerQuery.class);

    private int schemaId;
    private int publicationId;
    private int maxResults;
    private String sort;
    private int start;
    private int pageSize;
    private Multimap<String, String> keywordFilters = ArrayListMultimap.create();

    // TODO: Add filters for custom meta data as well

    private boolean hasMore;

    public int getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(int schemaId) {
        this.schemaId = schemaId;
    }

    public int getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(int publicationId) {
        this.publicationId = publicationId;
    }

    public int getMaxResults() {
        return maxResults;
    }

    public void setMaxResults(int maxResults) {
        this.maxResults = maxResults;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public Multimap<String, String> getKeywordFilters() {
        return keywordFilters;
    }

    public void setKeywordFilters(Multimap<String, String> keywordFilters) {
        this.keywordFilters = keywordFilters;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    // TODO: Why bound this only to teasers???
    public List<Teaser> executeQuery() throws BrokerQueryException {
        final Query query = new Query(buildCriteria());

        if (!Strings.isNullOrEmpty(sort) && !sort.toLowerCase().equals("none")) {
            query.addSorting(getSortParameter());
        }

        if (maxResults > 0) {
            query.setResultFilter(new LimitFilter(maxResults));
        }

        if (pageSize > 0) {
            // We set the page size to one more than what we need, to see if there are more pages to come...
            query.setResultFilter(new PagingFilter(start, pageSize + 1));
        }

        try {
            final String[] ids = query.executeQuery();

            final ComponentMetaFactory cmf = new ComponentMetaFactory(publicationId);
            final List<Teaser> results = new ArrayList<>();

            hasMore = ids.length > pageSize;

            int count = 0;
            for (String compId : ids) {
                if (count < pageSize) {
                    final ComponentMeta compMeta = cmf.getMeta(compId);
                    if (compMeta != null) {
                        results.add(getTeaserFromMeta(compMeta));
                    }
                    count++;
                } else {
                    break;
                }
            }

            return results;
        } catch (StorageException e) {
            throw new BrokerQueryException("Exception while executing broker query", e);
        }
    }

    private Criteria buildCriteria() {
        final List<Criteria> children = new ArrayList<>();

        if (schemaId > 0) {
            children.add(new ItemSchemaCriteria(schemaId));
        }

        if (publicationId > 0) {
            children.add(new PublicationCriteria(publicationId));
        }

        for (Map.Entry<String, String> entry : keywordFilters.entries()) {
            children.add(new TaxonomyKeywordCriteria(entry.getKey(), entry.getValue(), true));
        }

        return new AndCriteria(children.toArray(new Criteria[children.size()]));
    }

    private SortParameter getSortParameter() {
        SortDirection dir = sort.toLowerCase().endsWith("asc") ? SortDirection.ASCENDING : SortDirection.DESCENDING;
        return new SortParameter(getSortColumn(), dir);
    }

    private SortColumn getSortColumn() {
        final String sortTrim = sort.trim();
        final int pos = sortTrim.indexOf(' ');
        final String sortCol = pos > 0 ? sortTrim.substring(0, pos) : sortTrim;
        switch (sortCol.toLowerCase()) {
            case "title":
                return SortParameter.ITEMS_TITLE;

            case "pubdate":
                return SortParameter.ITEMS_LAST_PUBLISHED_DATE;

            default:
                // Default is to assume that its a custom metadata date field
                return new CustomMetaKeyColumn(sort, MetadataType.DATE);
        }
    }

    private Teaser getTeaserFromMeta(ComponentMeta compMeta) {
        final Teaser result = new Teaser();

        final Link link = new Link();
        link.setUrl("tcm:" + compMeta.getPublicationId() + "-" + compMeta.getId());
        result.setLink(link);

        final CustomMeta customMeta = compMeta.getCustomMeta();

        final DateTime date = getDateFromCustomMeta(customMeta, "dateCreated");
        result.setDate(date != null ? date : new DateTime(compMeta.getLastPublicationDate()));

        final String headline = getTextFromCustomMeta(customMeta, "name");
        result.setHeadline(headline != null ? headline : compMeta.getTitle());

        result.setText(getTextFromCustomMeta(customMeta, "introText"));

        return result;
    }

    private String getTextFromCustomMeta(CustomMeta meta, String fieldName) {
        return meta.getNameValues().containsKey(fieldName) ? meta.getFirstValue(fieldName).toString() : null;
    }

    private DateTime getDateFromCustomMeta(CustomMeta meta, String fieldName) {
        if (meta.getNameValues().containsKey(fieldName)) {
            Object firstValue = meta.getFirstValue(fieldName);
            if (!firstValue.equals("")) {
                return new DateTime(firstValue);
            }
        }

        return null;
    }
}
