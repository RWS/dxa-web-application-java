package com.sdl.dxa.tridion.broker;

import com.google.common.base.Strings;
import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.contentmodel.Pagination;
import com.sdl.web.pca.client.contentmodel.enums.ContentIncludeMode;
import com.sdl.web.pca.client.contentmodel.generated.InputItemFilter;
import com.sdl.web.pca.client.contentmodel.generated.InputSchemaCriteria;
import com.sdl.web.pca.client.contentmodel.generated.InputSortParam;
import com.sdl.web.pca.client.contentmodel.generated.Item;
import com.sdl.web.pca.client.contentmodel.generated.ItemConnection;
import com.sdl.web.pca.client.contentmodel.generated.SortFieldType;
import com.sdl.web.pca.client.contentmodel.generated.SortOrderType;
import com.sdl.webapp.common.api.WebRequestContext;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.support.SimpleValueWrapper;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static com.sdl.web.pca.client.contentmodel.generated.FilterItemType.COMPONENT;

public class GraphQLQueryProvider implements QueryProvider {

    private ApiClientProvider clientProvider;
    private boolean hasMore;
    private String cursor;
    private final Cache queryCache;
    private final WebRequestContext webRequestContext;

    public GraphQLQueryProvider(ApiClientProvider clientProvider,
                                CacheManager cacheManager,
                                WebRequestContext webRequestContext) {
        this.clientProvider = clientProvider;
        this.queryCache = cacheManager.getCache("queryCache");
        this.webRequestContext = webRequestContext;
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
        String key = queryParams.toString();

        SimpleValueWrapper simpleValueWrapper = null;
        if (!webRequestContext.isSessionPreview()) {
            simpleValueWrapper = (SimpleValueWrapper) queryCache.get(key);
        }

        List<Item> result;
        if (simpleValueWrapper != null) {
            //Query result is in cache
            result = (List<Item>) simpleValueWrapper.get();
        } else {
            //Not in cache, query from backend
            InputItemFilter filter = buildFilter(queryParams);
            InputSortParam sort = buildSort(queryParams);
            int pageSize = queryParams.getPageSize() > 0 ? queryParams.getPageSize() + 1 : queryParams.getPageSize();
            Pagination pagination = new Pagination();
            pagination.setFirst(pageSize);
            pagination.setAfter(queryParams.getCursor());
            ItemConnection results = clientProvider.getClient().executeItemQuery(filter, sort, pagination, null, ContentIncludeMode.EXCLUDE, false, null);
            List<Item> resultList = results.getEdges().stream().map(edge -> edge.getNode()).collect(Collectors.toList());

            if (pageSize == -1) {
                cursor = null;
                return resultList;
            }
            hasMore = results.getEdges().size() > queryParams.getPageSize();
            int n = hasMore ? queryParams.getPageSize() : results.getEdges().size();
            cursor = n > 0 ? results.getEdges().get(n - 1).getCursor() : null;
            result = hasMore ? resultList.subList(0, queryParams.getPageSize()) : resultList;

            if (!webRequestContext.isSessionPreview()) {
                queryCache.put(key, result);
            }
        }
        return result;
    }

    private InputItemFilter buildFilter(SimpleBrokerQuery queryParams) {
        InputItemFilter filter = new InputItemFilter();
        filter.setItemTypes(Arrays.asList(COMPONENT));

        if (queryParams.getSchemaId() > 0) {
            InputSchemaCriteria schema = new InputSchemaCriteria();
            schema.setId("" + queryParams.getSchemaId());
            filter.setSchema(schema);
        }

        if (queryParams.getPublicationId() > 0) {
            filter.setPublicationIds(Arrays.asList(queryParams.getPublicationId()));
        }
/** @ToDo https://github.com/sdl/dxa-web-application-java/issues/129
        if (queryParams.getKeywordFilters() != null) {
            List<InputItemFilter> extraTaxonolies = new ArrayList<>();
            queryParams.getKeywordFilters().entries().forEach(entry -> {
                InputItemFilter extraFilter = new InputItemFilter();
                extraFilter.setItemTypes(Arrays.asList(KEYWORD));
                TaxonomyKeywordCriteria tkc = new TaxonomyKeywordCriteria(entry.getKey(), entry.getValue(), true);
                InputKeywordCriteria keywordCriteria = new InputKeywordCriteria();
                keywordCriteria.setKey(entry.getKey());
                keywordCriteria.setKeywordId(tkc.getKeywordId());
                extraFilter.setKeyword(keywordCriteria);
            });
            filter.setAnd(extraTaxonolies);
        }
*/
        return filter;
    }

    private InputSortParam buildSort(SimpleBrokerQuery queryParams) {
        if (Strings.isNullOrEmpty(queryParams.getSort()) ||
            "none".equalsIgnoreCase(queryParams.getSort())) {
            return null;
        }

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
}
