package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.api.content.ContentProviderException;
import com.sdl.webapp.common.api.model.Entity;
import com.sdl.webapp.common.api.model.entity.ContentList;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.GenericComponent;
import org.springframework.stereotype.Component;

import java.util.Map;

import static com.sdl.webapp.dd4t.entityfactory.FieldUtil.getIntValue;
import static com.sdl.webapp.dd4t.entityfactory.FieldUtil.getStringValue;

@Component
public class ContentListFactory implements EntityFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { ContentList.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityClass)
            throws ContentProviderException {
        final ContentList contentList = new ContentList();

        final GenericComponent component = componentPresentation.getComponent();
        final Map<String, Field> content = component.getContent();
        final Map<String, Field> metadata = component.getMetadata();

        contentList.setHeadline(getStringValue(content, "headline"));
        // TODO: link, pageSize, contentType, sort, start, currentPage, hasMore, itemListElements

        contentList.setPageSize(getIntValue(metadata, "pageSize", 0));

        return contentList;
    }
}
