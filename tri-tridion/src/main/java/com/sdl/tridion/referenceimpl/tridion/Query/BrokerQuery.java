package com.sdl.tridion.referenceimpl.tridion.Query;

import com.sdl.tridion.referenceimpl.common.model.entity.Teaser;
import com.tridion.broker.querying.criteria.Criteria;
import com.tridion.broker.querying.Query;
import com.tridion.broker.querying.sorting.SortColumn;
import com.tridion.broker.querying.sorting.SortDirection;
import com.tridion.broker.querying.sorting.SortParameter;
import com.tridion.broker.querying.sorting.column.CustomMetaKeyColumn;

import com.tridion.broker.querying.MetadataType;

import java.util.Dictionary;
import java.util.List;

public class BrokerQuery {
    private int schemaId;
    private int publicationId;
    private int maxResults;
    private String sort;
    private int start;
    private int pageSize;
    public Dictionary<String, List<String>> KeywordFilters;
    private boolean hasMore;

    public int getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(int schemaId) {
        this.schemaId = schemaId;
    }

    public List<Teaser> ExecuteQuery()
    {
        Criteria criteria = BuildCriteria();
        Query query = new Query(criteria);

        if (!sort.isEmpty() && !sort.toLowerCase().equals("none")) {
            query.addSorting(GetSortParameter());
        }


        return null;
    }

    private Criteria BuildCriteria() {

        return null;
    }

    private SortParameter GetSortParameter()
    {
        SortDirection sortDirection= (sort.toLowerCase().endsWith("asc")) ? SortParameter.ASCENDING : SortParameter.DESCENDING;
        return new SortParameter(GetSortColumn(), sortDirection);
    }

    private SortColumn GetSortColumn()
    {
        //TODO add more options if required
        int pos = sort.trim().indexOf(" ");

        String sorting = pos > 0 ? sort.trim().substring(0, pos) : sort.trim();
        switch (sorting.toLowerCase())
        {
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
