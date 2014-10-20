package com.sdl.tridion.referenceimpl.dd4t.entityfactory;

import com.sdl.tridion.referenceimpl.common.model.Entity;
import com.sdl.tridion.referenceimpl.common.model.entity.MediaItem;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.Multimedia;

public abstract class MediaItemFactory implements EntityFactory {

    protected Entity createBaseEntity(ComponentPresentation componentPresentation, MediaItem entity) {
        final GenericComponent component = componentPresentation.getComponent();

        final Multimedia multimedia = component.getMultimedia();
        if (multimedia != null && multimedia.getUrl() != null) {
            entity.setUrl(multimedia.getUrl());
            entity.setFileName(multimedia.getFileName());
            entity.setFileSize(multimedia.getSize());
            entity.setMimeType(multimedia.getMimeType());
        }

        return entity;
    }
}
