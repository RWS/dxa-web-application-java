package com.sdl.webapp.tridion.mapping;

import com.sdl.web.api.broker.querying.BrokerQuery;
import com.sdl.web.api.broker.querying.QueryImpl;
import com.sdl.web.api.broker.querying.criteria.Criteria;
import com.sdl.web.api.broker.querying.criteria.content.ItemSchemaCriteria;
import com.sdl.web.api.broker.querying.criteria.content.PublicationCriteria;
import com.sdl.web.api.broker.querying.criteria.operators.AndCriteria;
import com.sdl.web.api.broker.querying.criteria.taxonomy.TaxonomyKeywordCriteria;
import com.sdl.web.api.broker.querying.filter.LimitFilter;
import com.sdl.web.api.broker.querying.filter.PagingFilter;
import com.sdl.web.api.broker.querying.sorting.BrokerSortColumn;
import com.sdl.web.api.broker.querying.sorting.CustomMetaKeyColumn;
import com.sdl.web.api.broker.querying.sorting.SortDirection;
import com.sdl.web.api.broker.querying.sorting.SortParameter;
import com.sdl.web.api.content.BinaryContentRetriever;
import com.sdl.web.api.dynamic.DynamicMetaRetriever;
import com.sdl.web.api.meta.WebComponentMetaFactory;
import com.sdl.web.api.meta.WebComponentMetaFactoryImpl;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.LinkResolver;
import com.sdl.webapp.common.api.content.StaticContentNotFoundException;
import com.sdl.webapp.common.api.model.query.ComponentMetadata;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.util.ImageUtils;
import com.tridion.broker.StorageException;
import com.tridion.broker.querying.MetadataType;
import com.tridion.data.BinaryData;
import com.tridion.meta.BinaryMeta;
import com.tridion.meta.ComponentMeta;
import com.tridion.meta.NameValuePair;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.google.common.base.Strings.isNullOrEmpty;
import static com.sdl.webapp.common.util.ImageUtils.writeToFile;

@Component
@Slf4j
@SuppressWarnings("Duplicates")
public class DefaultProvider extends AbstractDefaultProvider {

    private static final Object LOCK = new Object();

    private static final Logger LOG = LoggerFactory.getLogger(DefaultProvider.class);

    @Autowired
    private DynamicMetaRetriever dynamicMetaRetriever;

    @Autowired
    private BinaryContentRetriever binaryContentRetriever;

    @Autowired
    private LinkResolver linkResolver;

    @Override
    protected DefaultProvider.StaticContentFile getStaticContentFile(File file, ImageUtils.StaticContentPathInfo pathInfo, int publicationId) throws ContentProviderException, IOException {
        BinaryMeta binaryMeta;
        WebComponentMetaFactory factory = new WebComponentMetaFactoryImpl(publicationId);
        ComponentMeta componentMeta;
        int itemId;

        synchronized (LOCK) {
            binaryMeta = dynamicMetaRetriever.getBinaryMetaByURL(prependFullUrlIfNeeded(pathInfo.getFileName()));
            if (binaryMeta == null) {
                throw new StaticContentNotFoundException("No binary meta found for: [" + publicationId + "] " +
                        pathInfo.getFileName());
            }
            itemId = (int) binaryMeta.getURI().getItemId();
            componentMeta = factory.getMeta(itemId);
            if (componentMeta == null) {
                throw new StaticContentNotFoundException("No meta meta found for: [" + publicationId + "] " +
                        pathInfo.getFileName());
            }
        }

        long componentTime = componentMeta.getLastPublicationDate().getTime();
        if (isToBeRefreshed(file, componentTime)) {
            BinaryData binaryData = binaryContentRetriever.getBinary(publicationId, itemId, binaryMeta.getVariantId());

            LOG.debug("Writing binary content to file: {}", file);
            writeToFile(file, pathInfo, binaryData.getBytes());
        } else {
            LOG.debug("File does not need to be refreshed: {}", file);
        }

        return new StaticContentFile(file, binaryMeta.getType());
    }

    @Override
    protected List<ComponentMetadata> executeQuery(SimpleBrokerQuery simpleBrokerQuery) {
        BrokerQuery query = new QueryImpl(buildCriteria(simpleBrokerQuery));

        if (!isNullOrEmpty(simpleBrokerQuery.getSort()) &&
                !Objects.equals(simpleBrokerQuery.getSort().toLowerCase(), "none")) {
            query.addSorting(getSortParameter(simpleBrokerQuery));
        }

        int maxResults = simpleBrokerQuery.getResultLimit();
        if (maxResults > 0) {
            query.setResultFilter(new LimitFilter(maxResults));
        }

        int pageSize = simpleBrokerQuery.getPageSize();
        if (pageSize > 0) {
            // We set the page size to one more than what we need, to see if there are more pages to come...
            query.setResultFilter(new PagingFilter(simpleBrokerQuery.getStartAt(), pageSize + 1));
        }

        final String[] ids;
        try {
            ids = query.executeQuery();
        } catch (StorageException e) {
            log.warn("Exception while execution of broker query", e);
            return Collections.emptyList();
        }

        final WebComponentMetaFactory cmf = new WebComponentMetaFactoryImpl(simpleBrokerQuery.getPublicationId());
        final List<ComponentMetadata> results = new ArrayList<>();

        simpleBrokerQuery.setHasMore(ids.length > pageSize);

        for (int i = 0; i < ids.length && i < pageSize; i++) {
            final ComponentMeta componentMeta = cmf.getMeta(ids[i]);
            if (componentMeta != null) {
                results.add(convert(componentMeta));
            }
        }

        return results;
    }

    private Criteria buildCriteria(@NotNull SimpleBrokerQuery query) {
        final List<Criteria> children = new ArrayList<>();

        if (query.getSchemaId() > 0) {
            children.add(new ItemSchemaCriteria(query.getSchemaId()));
        }

        if (query.getPublicationId() > 0) {
            children.add(new PublicationCriteria(query.getPublicationId()));
        }

        for (Map.Entry<String, String> entry : query.getKeywordFilters().entries()) {
            children.add(new TaxonomyKeywordCriteria(entry.getKey(), entry.getValue(), true));
        }

        return new AndCriteria(children);
    }

    private SortParameter getSortParameter(SimpleBrokerQuery simpleBrokerQuery) {
        SortDirection dir = simpleBrokerQuery.getSort().toLowerCase().endsWith("asc") ?
                SortDirection.ASCENDING : SortDirection.DESCENDING;
        return new SortParameter(getSortColumn(simpleBrokerQuery), dir);
    }

    private BrokerSortColumn getSortColumn(SimpleBrokerQuery simpleBrokerQuery) {
        final String sortTrim = simpleBrokerQuery.getSort().trim();
        final int pos = sortTrim.indexOf(' ');
        final String sortCol = pos > 0 ? sortTrim.substring(0, pos) : sortTrim;
        switch (sortCol.toLowerCase()) {
            case "title":
                return SortParameter.ITEMS_TITLE;

            case "pubdate":
                return SortParameter.ITEMS_LAST_PUBLISHED_DATE;

            default:
                // Default is to assume that its a custom metadata date field
                return new CustomMetaKeyColumn(simpleBrokerQuery.getSort(), MetadataType.DATE);
        }
    }

    private ComponentMetadata convert(ComponentMeta compMeta) {
        Map<String, ComponentMetadata.MetaEntry> custom = new HashMap<>(compMeta.getCustomMeta().getNameValues().size());
        for (Map.Entry<String, NameValuePair> entry : compMeta.getCustomMeta().getNameValues().entrySet()) {
            ComponentMetadata.MetaType metaType;
            switch (entry.getValue().getMetadataType()) {
                case DATE:
                    metaType = ComponentMetadata.MetaType.DATE;
                    break;
                case FLOAT:
                    metaType = ComponentMetadata.MetaType.FLOAT;
                    break;
                default:
                    metaType = ComponentMetadata.MetaType.STRING;
            }
            custom.put(entry.getKey(), ComponentMetadata.MetaEntry.builder()
                    .metaType(metaType)
                    .value(entry.getValue().getFirstValue())
                    .build());
        }

        return ComponentMetadata.builder()
                .id(String.valueOf(compMeta.getId()))
                .componentUrl(linkResolver.resolveLink("tcm:" + compMeta.getPublicationId() + '-' + compMeta.getId(), null))
                .publicationId(String.valueOf(compMeta.getPublicationId()))
                .owningPublicationId(String.valueOf(compMeta.getOwningPublicationId()))
                .schemaId(String.valueOf(compMeta.getSchemaId()))
                .title(compMeta.getTitle())
                .modificationDate(compMeta.getModificationDate())
                .initialPublicationDate(compMeta.getInitialPublicationDate())
                .lastPublicationDate(compMeta.getLastPublicationDate())
                .creationDate(compMeta.getCreationDate())
                .author(compMeta.getAuthor())
                .multimedia(compMeta.isMultimedia())
                .custom(custom)
                .build();
    }

}
