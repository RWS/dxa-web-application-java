package com.sdl.webapp.tridion.query;

import com.google.common.base.Strings;
import com.sdl.dxa.modules.core.model.entity.Teaser;
import com.sdl.webapp.common.api.model.RichText;
import com.sdl.webapp.common.api.model.entity.Link;
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
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
public class BrokerQueryImpl extends BrokerQuery {

    @Override
    public List<Teaser> executeQuery() throws BrokerQueryException {
        final Query query = new Query(buildCriteria());

        if (!Strings.isNullOrEmpty(getSort()) && !getSort().toLowerCase().equals("none")) {
            query.addSorting(getSortParameter());
        }

        int maxResults = getMaxResults();
        if (maxResults > 0) {
            query.setResultFilter(new LimitFilter(maxResults));
        }

        int pageSize = getPageSize();
        if (pageSize > 0) {
            // We set the page size to one more than what we need, to see if there are more pages to come...
            query.setResultFilter(new PagingFilter(getStart(), pageSize + 1));
        }

        try {
            final String[] ids = query.executeQuery();

            final ComponentMetaFactory cmf = new ComponentMetaFactory(getPublicationId());
            final List<Teaser> results = new ArrayList<>();

            setHasMore(ids.length > pageSize);

            int count = 0;
            for (String compId : ids) {
                if (count < pageSize) {
                    final ComponentMeta compMeta = cmf.getMeta(compId);
                    if (compMeta != null) {
                        results.add(getTeaser(compMeta));
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

        if (getSchemaId() > 0) {
            children.add(new ItemSchemaCriteria(getSchemaId()));
        }

        if (getPublicationId() > 0) {
            children.add(new PublicationCriteria(getPublicationId()));
        }

        for (Map.Entry<String, String> entry : getKeywordFilters().entries()) {
            children.add(new TaxonomyKeywordCriteria(entry.getKey(), entry.getValue(), true));
        }

        return new AndCriteria(children.toArray(new Criteria[children.size()]));
    }

    private SortParameter getSortParameter() {
        SortDirection dir = getSort().toLowerCase().endsWith("asc") ? SortDirection.ASCENDING : SortDirection.DESCENDING;
        return new SortParameter(getSortColumn(), dir);
    }

    private SortColumn getSortColumn() {
        final String sortTrim = getSort().trim();
        final int pos = sortTrim.indexOf(' ');
        final String sortCol = pos > 0 ? sortTrim.substring(0, pos) : sortTrim;
        switch (sortCol.toLowerCase()) {
            case "title":
                return SortParameter.ITEMS_TITLE;

            case "pubdate":
                return SortParameter.ITEMS_LAST_PUBLISHED_DATE;

            default:
                // Default is to assume that its a custom metadata date field
                return new CustomMetaKeyColumn(getSort(), MetadataType.DATE);
        }
    }

    protected String getTextFromCustomMeta(CustomMeta meta, String fieldName) {
        return meta.getNameValues().containsKey(fieldName) ? meta.getFirstValue(fieldName).toString() : null;
    }

    protected DateTime getDateFromCustomMeta(CustomMeta meta, String fieldName) {
        if (meta.getNameValues().containsKey(fieldName)) {
            Object firstValue = meta.getFirstValue(fieldName);
            if (!firstValue.equals("")) {
                return new DateTime(firstValue);
            }
        }

        return null;
    }

    private Teaser getTeaser(ComponentMeta compMeta) {
        final Teaser result = new Teaser();

        final Link link = new Link();
        link.setUrl("tcm:" + compMeta.getPublicationId() + "-" + compMeta.getId());
        result.setLink(link);

        final CustomMeta customMeta = compMeta.getCustomMeta();

        final DateTime date = getDateFromCustomMeta(customMeta, "dateCreated");
        result.setDate(date != null ? date : new DateTime(compMeta.getLastPublicationDate()));

        final String headline = getTextFromCustomMeta(customMeta, "name");
        result.setHeadline(headline != null ? headline : compMeta.getTitle());

        result.setText(new RichText(getTextFromCustomMeta(customMeta, "introText")));

        return result;
    }
}
