package com.sdl.dxa.tridion.broker;

import com.google.common.base.Strings;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.Pagination;
import com.sdl.web.pca.client.contentmodel.enums.ContentIncludeMode;
import com.sdl.web.pca.client.contentmodel.generated.FilterItemType;
import com.sdl.web.pca.client.contentmodel.generated.InputItemFilter;
import com.sdl.web.pca.client.contentmodel.generated.InputSchemaCriteria;
import com.sdl.web.pca.client.contentmodel.generated.InputSortParam;
import com.sdl.web.pca.client.contentmodel.generated.Item;
import com.sdl.web.pca.client.contentmodel.generated.ItemConnection;
import com.sdl.web.pca.client.contentmodel.generated.SortFieldType;
import com.sdl.web.pca.client.contentmodel.generated.SortOrderType;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class GraphQLQueryProvider implements QueryProvider {

    private ApiClient client;

    private boolean hasMore;

    private String cursor;

    public GraphQLQueryProvider(ApiClient client) {
        this.client = client;
    }

    @Override
    public boolean hasMore() {
        return hasMore;
    }

    @Override
    public String getCursor() {
        return cursor;
    }

    @Override
    public List<Item> executeQueryItems(SimpleBrokerQuery queryParams) {
        InputItemFilter filter = buildFilter(queryParams);
        InputSortParam sort = buildSort(queryParams);
        int pageSize = queryParams.getPageSize() > 0 ? queryParams.getPageSize() + 1 : queryParams.getPageSize();
        Pagination pagination = new Pagination();
        pagination.setFirst(pageSize);
        pagination.setAfter(queryParams.getCursor());
        ItemConnection results = client.executeItemQuery(filter, sort, pagination, null, ContentIncludeMode.EXCLUDE, false, null);
        List<Item> resultList = results.getEdges().stream().map(edge -> edge.getNode()).collect(Collectors.toList());

        if (pageSize == -1) {
            cursor = null;
            return resultList;
        }
        hasMore = results.getEdges().size() > queryParams.getPageSize();
        int n = hasMore ? queryParams.getPageSize() : results.getEdges().size();
        cursor = n > 0 ? results.getEdges().get(n - 1).getCursor() : null;

        return hasMore ? resultList.subList(0, queryParams.getPageSize()) : resultList;
    }

    private InputItemFilter buildFilter(SimpleBrokerQuery queryParams) {
        InputItemFilter filter = new InputItemFilter();
        filter.setItemTypes(Arrays.asList(FilterItemType.COMPONENT));

        if (queryParams.getSchemaId() > 0) {
            InputSchemaCriteria schema = new InputSchemaCriteria();
            schema.setId("" + queryParams.getSchemaId());
            filter.setSchema(schema);
        }

        if (queryParams.getPublicationId() > 0) {
            filter.setPublicationIds(Arrays.asList(queryParams.getPublicationId()));
        }
        return filter;
    }

    private InputSortParam buildSort(SimpleBrokerQuery queryParams) {
        if (!Strings.isNullOrEmpty(queryParams.getSort()) && !"none".equalsIgnoreCase(queryParams.getSort())) {

            InputSortParam sort = new InputSortParam();

            sort.setOrder(queryParams.getSort().toLowerCase().endsWith("asc") ?
                    SortOrderType.Ascending : SortOrderType.Descending);
            int idx = queryParams.getSort().trim().indexOf(" ");
            String sortColumn = idx > 0 ? queryParams.getSort().trim().substring(0, idx) : queryParams.getSort().trim();

            switch (sortColumn.toLowerCase()) {
                case "title":
                    sort.setSortBy(SortFieldType.TITLE);
                    break;
                case "pubdate":
                    sort.setSortBy(SortFieldType.LAST_PUBLISH_DATE);
                    break;
                default:
                    sort.setSortBy(SortFieldType.CREATION_DATE);
                    break;
            }
            return sort;
        }
        return null;
    }
}
