package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.mapping.config.SemanticField;
import com.sdl.webapp.common.api.model.entity.Download;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.YouTubeVideo;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MultimediaLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.MultimediaLink };

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    public Object getFieldValue(SemanticField semanticField, BaseField field, TypeDescriptor targetType)
            throws FieldConverterException {
        final List<Object> fieldValues = new ArrayList<>();

        final Class<?> targetClass = targetType.isCollection() ? targetType.getElementTypeDescriptor().getType() :
                targetType.getType();

        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
            final Object fieldValue;

            // TODO: Find a better way to determine the media item type instead of looking at the schema title
            final String schemaTitle = component.getSchema().getTitle().toLowerCase();

            if (targetClass.isAssignableFrom(YouTubeVideo.class) && schemaTitle.contains("youtube")) {
                fieldValue = createYouTubeEntity((GenericComponent) component);
            } else if (targetClass.isAssignableFrom(Download.class) && schemaTitle.contains("download")) {
                fieldValue = createDownloadEntity((GenericComponent) component);
            } else if (targetClass.isAssignableFrom(Image.class)) {
                fieldValue = createImageEntity((GenericComponent) component);
            } else {
                throw new UnsupportedTargetTypeException("Unsupported target type for multimedia link field: " +
                        targetClass.getName());
            }

            if (fieldValue != null) {
                fieldValues.add(fieldValue);
            }
        }

        return semanticField.isMultiValue() ? fieldValues : (fieldValues.isEmpty() ? null : fieldValues.get(0));
    }

    private Image createImageEntity(GenericComponent component) {
        final Image image = new Image();
        fillMediaItemFields(component, image);
        image.setAlternateText(component.getMultimedia().getAlt());
        return image;
    }

    private Download createDownloadEntity(GenericComponent component) {
        final Download download = new Download();
        fillMediaItemFields(component, download);
        download.setDescription(FieldUtils.getStringValue(component.getMetadata(), "description"));
        return download;
    }

    private YouTubeVideo createYouTubeEntity(GenericComponent component) {
        YouTubeVideo youTubeVideo = new YouTubeVideo();
        fillMediaItemFields(component, youTubeVideo);
        youTubeVideo.setYouTubeId(FieldUtils.getStringValue(component.getMetadata(), "youTubeId"));
        return youTubeVideo;
    }

    private void fillMediaItemFields(GenericComponent component, MediaItem mediaItem) {
        final Multimedia multimedia = component.getMultimedia();

        String url = multimedia.getUrl();
        if (url.startsWith("/")) {
            url = url.substring(1);
        }
        mediaItem.setUrl(url);

        mediaItem.setFileName(multimedia.getFileName());
        mediaItem.setFileSize(multimedia.getSize());
        mediaItem.setMimeType(multimedia.getMimeType());
    }
}
