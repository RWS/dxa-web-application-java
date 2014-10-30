package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.model.entity.Download;
import com.sdl.webapp.common.api.model.entity.Image;
import com.sdl.webapp.common.api.model.entity.MediaItem;
import com.sdl.webapp.common.api.model.entity.YouTubeVideo;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.GenericComponent;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.contentmodel.impl.BaseField;
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
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<MediaItem> mediaItems = new ArrayList<>();

        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
            final MediaItem mediaItem;

            // TODO: Find a better way to determine the media item type instead of looking at the schema title
            final String schemaTitle = component.getSchema().getTitle().toLowerCase();

            if (targetClass.isAssignableFrom(YouTubeVideo.class) && schemaTitle.contains("youtube")) {
                mediaItem = createYouTubeEntity((GenericComponent) component);
            } else if (targetClass.isAssignableFrom(Download.class) && schemaTitle.contains("download")) {
                mediaItem = createDownloadEntity((GenericComponent) component);
            } else if (targetClass.isAssignableFrom(Image.class)) {
                mediaItem = createImageEntity((GenericComponent) component);
            } else {
                throw new UnsupportedTargetTypeException("Unsupported target type for multimedia link field: " +
                        targetClass.getName());
            }

            if (mediaItem != null) {
                mediaItems.add(mediaItem);
            }
        }

        return mediaItems;
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
