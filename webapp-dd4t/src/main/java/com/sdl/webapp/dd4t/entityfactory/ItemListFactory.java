package com.sdl.webapp.dd4t.entityfactory;

import com.sdl.webapp.common.ContentProviderException;
import com.sdl.webapp.common.model.Entity;
import com.sdl.webapp.common.model.entity.Image;
import com.sdl.webapp.common.model.entity.ItemList;
import com.sdl.webapp.common.model.entity.Teaser;
import org.dd4t.contentmodel.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sdl.webapp.dd4t.entityfactory.FieldUtil.*;

@Component
public class ItemListFactory implements EntityFactory {

    private static final Class<?>[] SUPPORTED_ENTITY_TYPES = { ItemList.class };

    @Override
    public Class<?>[] supportedEntityTypes() {
        return SUPPORTED_ENTITY_TYPES;
    }

    @Override
    public Entity createEntity(ComponentPresentation componentPresentation, Class<?> entityType)
            throws ContentProviderException {
        final ItemList itemList = new ItemList();

        final GenericComponent component = componentPresentation.getComponent();
        final Map<String, Field> content = component.getContent();

        itemList.setHeadline(FieldUtil.getStringValue(content, "headline"));

        List<Teaser> itemListElements = new ArrayList<>();
        for (FieldSet fieldSet : getEmbeddedValues(content, "itemListElement")) {
            itemListElements.add(createItemListElement(fieldSet));
        }
        itemList.setItemListElements(itemListElements);

        return itemList;
    }

    private Teaser createItemListElement(FieldSet fieldSet) {
        final Teaser teaser = new Teaser();

        final Map<String, Field> content = fieldSet.getContent();

        teaser.setHeadline(getStringValue(content, "subheading"));
        teaser.setText(getStringValue(content, "content"));

        final GenericComponent linkedComponent = getComponentValue(content, "media");
        final Multimedia multimedia = linkedComponent.getMultimedia();
        if (multimedia != null) {
            final Image image = new Image();
            image.setUrl(multimedia.getUrl());
            image.setFileName(multimedia.getFileName());
            image.setFileSize(multimedia.getSize());
            image.setMimeType(multimedia.getMimeType());

            teaser.setMedia(image);
        }

        // TODO: more fields

        return teaser;
    }
}
