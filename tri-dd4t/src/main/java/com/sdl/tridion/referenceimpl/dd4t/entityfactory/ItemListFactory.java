package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import com.sdl.tridion.referenceimpl.common.ContentProviderException;
import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.entity.ItemList;
import com.sdl.tridion.referenceimpl.common.model.entity.Teaser;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.GenericComponent;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.sdl.tridion.referenceimpl.dd4t.entityfactory.FieldUtil.getEmbeddedValues;
import static com.sdl.tridion.referenceimpl.dd4t.entityfactory.FieldUtil.getStringValue;

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

        // TODO: more fields

        return teaser;
    }
}
