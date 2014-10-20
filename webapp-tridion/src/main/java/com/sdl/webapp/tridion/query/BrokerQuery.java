package com.sdl.webapp.tridion.query;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.sdl.webapp.common.model.entity.Link;
import com.sdl.webapp.common.model.entity.Teaser;
import com.tridion.broker.querying.MetadataType;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.criteria.content.ItemSchemaCriteria;
import com.tridion.broker.querying.criteria.content.ItemTypeCriteria;
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
import com.tridion.taxonomies.Keyword;
import com.tridion.taxonomies.TaxonomyFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BrokerQuery {
    private static final Logger LOG = LoggerFactory.getLogger(BrokerQuery.class);

    private int schemaId;
    private int publicationId;
    private int maxResults;
    private String sort;
    private int start;
    private int pageSize;
    private ListMultimap<String, String> keywordFilters;
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

    public ListMultimap<String, String> getKeywordFilters() {
        return keywordFilters;
    }

    public void setKeywordFilters(ListMultimap<String, String> keywordFilters) {
        this.keywordFilters = keywordFilters;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

    public List<Teaser> executeQuery() {
        Criteria criteria = buildCriteria();
        Query query = new Query(criteria);

        if (!sort.isEmpty() && !sort.toLowerCase().equals("none")) {
            query.addSorting(getSortParameter());
        }
        if (maxResults > 0) {
            query.setResultFilter(new LimitFilter(maxResults));
        }
        if (pageSize > 0) {
            //We set the page size to one more than what we need, to see if there are more pages to come...
            query.setResultFilter(new PagingFilter(start, pageSize + 1));
        }

        try {
            ComponentMetaFactory cmf = new ComponentMetaFactory(publicationId);
            List<Teaser> results = new ArrayList<>();
            String[] ids = query.executeQuery();
            hasMore = ids.length > pageSize;
            int count = 0;

            for (String compId : ids) {
                if (count < pageSize) {
                    ComponentMeta compMeta = cmf.getMeta(compId);
                    if (compMeta != null) {
                        results.add(getTeaserFromMeta(compMeta));
                    }
                    count++;
                } else {
                    break;
                }
            }
            return results;
        } catch (Exception ex) {
            LOG.error(String.format("Error running Broker query: {0}", ex.getMessage()), ex);
        }

        return null;
    }

    private Criteria buildCriteria() {
        List<Criteria> criterias = new ArrayList<Criteria>();

        criterias.add(new ItemTypeCriteria(16));

        if (schemaId > 0) {
            criterias.add(new ItemSchemaCriteria(schemaId));
        }
        if (publicationId > 0) {
            criterias.add(new PublicationCriteria(publicationId));
        }
        if (keywordFilters != null) {
            for (String taxonomy : keywordFilters.keySet()) {
                for (String keyword : keywordFilters.get(taxonomy)) {
                    criterias.add(new TaxonomyKeywordCriteria(taxonomy, keyword, true));
                }
            }
        }

        return new AndCriteria(criterias.toArray(new Criteria[criterias.size()]));
    }

    private static Teaser getTeaserFromMeta(ComponentMeta compMeta) {
        Link link = new Link();
        link.setUrl(String.format("tcm:{0}-{1}", compMeta.getPublicationId(), compMeta.getId()));

        Teaser teaser = new Teaser();
        teaser.setLink(link);

        Date date = getDateFromCustomMeta(compMeta.getCustomMeta(), "dateCreated");
        teaser.setDate(date != null ? date : compMeta.getLastPublicationDate());

        String headLine = getTextFromCustomMeta(compMeta.getCustomMeta(), "name");
        teaser.setHeadline(!headLine.isEmpty() ? headLine : compMeta.getTitle());

        teaser.setText(getTextFromCustomMeta(compMeta.getCustomMeta(), "introText"));

        return teaser;
    }

    private static String getTextFromCustomMeta(CustomMeta meta, String fieldname) {
        if (meta.getName().contains(fieldname)) {
            return meta.getValue(fieldname).toString();
        }
        return null;
    }

    private static Date getDateFromCustomMeta(CustomMeta meta, String fieldname) {
        if (meta.getName().contains(fieldname)) {
            return (Date) meta.getValue(fieldname);
        }
        return null;
    }

    public void setKeywordFilters(List<String> keywordUris) {
        TaxonomyFactory taxonomyFactory = new TaxonomyFactory();
        List<Keyword> keywords = new ArrayList<Keyword>();

        for (String kwUri : keywordUris) {
            Keyword kw = taxonomyFactory.getTaxonomyKeyword(kwUri);
            if (kw != null) {
                keywords.add(kw);
            }
        }

        if (keywordFilters == null) {
            keywordFilters = ArrayListMultimap.create();
        }

        for (Keyword kw : keywords) {
            keywordFilters.put(kw.getTaxonomyURI(), kw.getKeywordURI());
        }
    }

    public static Keyword loadKeyword(String keywordUri) {
        TaxonomyFactory taxonomyFactory = new TaxonomyFactory();
        return taxonomyFactory.getTaxonomyKeyword(keywordUri);
    }

    public static List<Keyword> loadKeywords(List<String> keywordUris) {
        List<Keyword> res = new ArrayList<Keyword>();
        TaxonomyFactory taxonomyFactory = new TaxonomyFactory();

        for (String uri : keywordUris) {
            Keyword kw = taxonomyFactory.getTaxonomyKeyword(uri);
            if (kw != null) {
                res.add(kw);
            }
        }
        return res;
    }

    private SortParameter getSortParameter() {
        SortDirection sortDirection = (sort.toLowerCase().endsWith("asc")) ? SortParameter.ASCENDING : SortParameter.DESCENDING;
        return new SortParameter(getSortColumn(), sortDirection);
    }

    private SortColumn getSortColumn() {
        //TODO add more options if required
        int pos = sort.trim().indexOf(" ");

        String sorting = pos > 0 ? sort.trim().substring(0, pos) : sort.trim();
        switch (sorting.toLowerCase()) {
            case "title":
                return SortParameter.ITEMS_TITLE;
            case "pubdate":
                return SortParameter.ITEMS_LAST_PUBLISHED_DATE;
            default:
                //Default is to assume that its a custom metadata date field;
                return new CustomMetaKeyColumn(sort, MetadataType.DATE);
        }
    }
}
