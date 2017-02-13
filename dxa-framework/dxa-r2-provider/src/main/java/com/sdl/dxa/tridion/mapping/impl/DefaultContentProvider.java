package com.sdl.dxa.tridion.mapping.impl;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.PageInclusion;
import com.sdl.webapp.common.api.content.ContentProvider;
import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.content.StaticContentItem;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.PageModel;
import com.sdl.webapp.common.api.model.entity.DynamicList;
import com.sdl.webapp.common.api.model.query.SimpleBrokerQuery;
import com.sdl.webapp.common.exceptions.DxaException;
import com.tridion.content.PageContentFactory;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import static com.sdl.webapp.common.util.LocalizationUtils.findPageByPath;
import static org.springframework.web.util.UriUtils.encodePath;

@Service("r2ContentProvider")
public class DefaultContentProvider implements ContentProvider {

    @Autowired
    private ModelBuilderPipeline builderPipeline;

    @Autowired
    private ObjectMapper objectMapper;

    @Nullable
//    @Override
    @SneakyThrows(UnsupportedEncodingException.class)
    public PageModel getPageModel(String path, Localization localization) throws ContentProviderException {
        return findPageByPath(encodePath(path, "UTF-8"), localization, (pagePath, localizationId) -> {
            PageModelData modelData = null;
            try {
                String pageContent = new PageContentFactory()
                        .getPageContent(localizationId, 640).getString();
                modelData = objectMapper.readValue(pageContent, PageModelData.class);
            } catch (JsonParseException e) {
                e.printStackTrace();
            } catch (JsonMappingException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            builderPipeline.createPageModel(modelData, PageInclusion.EXCLUDE /*todo*/, localization);
            return null;
        });
    }

    //    @Override
    public EntityModel getEntityModel(String tcmUri, Localization localization) throws ContentProviderException, DxaException {
        return null;
    }

    //    @Override
    public <T extends EntityModel> void populateDynamicList(DynamicList<T, SimpleBrokerQuery> dynamicList, Localization localization) throws ContentProviderException {

    }

    //    @Override
    public StaticContentItem getStaticContent(String path, String localizationId, String localizationPath) throws ContentProviderException {
        return null;
    }


    //todo do not use dd4t
//    private TCMURI loadTcmUri(final String url, final int localizationId) throws StorageException, ParseException {
////        Query query = new Query(new AndCriteria(new PublicationCriteria(localizationId), new PageURLCriteria(url)));
////        query.setResultFilter(new LimitFilter(1));
////        query.addSorting(new SortParameter(SortParameter.ITEMS_URL, SortParameter.DESCENDING));
////        return new TCMURI(query.executeQuery()[0]);
//    }
}
