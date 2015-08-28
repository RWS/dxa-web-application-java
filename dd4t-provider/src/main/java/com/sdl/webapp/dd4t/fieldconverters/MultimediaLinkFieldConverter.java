package com.sdl.webapp.dd4t.fieldconverters;

import com.sdl.webapp.common.api.model.entity.*;
import com.sdl.webapp.common.util.TcmUtils;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Multimedia;
import org.dd4t.contentmodel.impl.BaseField;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MultimediaLinkFieldConverter extends AbstractFieldConverter {

    private static final FieldType[] SUPPORTED_FIELD_TYPES = { FieldType.MULTIMEDIALINK };

    @Override
    public FieldType[] supportedFieldTypes() {
        return SUPPORTED_FIELD_TYPES;
    }

    @Override
    protected List<?> getFieldValues(BaseField field, Class<?> targetClass) throws FieldConverterException {
        final List<MediaItem> mediaItems = new ArrayList<>();

        for (org.dd4t.contentmodel.Component component : field.getLinkedComponentValues()) {
            final MediaItem mediaItem = createMediaItem(component, targetClass);
            if (mediaItem != null) {
                mediaItems.add(mediaItem);
            }
        }

        return mediaItems;
    }

    public MediaItem createMediaItem(org.dd4t.contentmodel.Component component, Class<?> targetClass) throws FieldConverterException {
        if (component.getMultimedia() == null) {
            return null;
        }

        final MediaItem mediaItem;

        // TODO: Find a better way to determine the media item type instead of looking at the schema title
        // TODO: Create a ECLItem if it is a ECL (by looking on the actual ECL URI
        //

        final String schemaTitle = component.getSchema().getTitle().toLowerCase();

        if (targetClass.isAssignableFrom(YouTubeVideo.class) && schemaTitle.contains("youtube")) {
            mediaItem = createYouTubeVideo(component);
        } else if (targetClass.isAssignableFrom(Download.class) && schemaTitle.contains("download")) {
            mediaItem = createDownload(component);
        } else if (targetClass.isAssignableFrom(Image.class)) {
            mediaItem = createImage(component);
        } else if (targetClass.isAssignableFrom(EclItem.class)) {
            mediaItem = createEclItem(component);
        }
        else {
            throw new UnsupportedTargetTypeException(targetClass);
        }

        return mediaItem;
    }

    private Image createImage(org.dd4t.contentmodel.Component component) {
        final Image image = new Image();
        fillMediaItemFields(component, image);
        image.setAlternateText(component.getMultimedia().getAlt());
        return image;
    }

    private Download createDownload(org.dd4t.contentmodel.Component component) {
        final Download download = new Download();
        fillMediaItemFields(component, download);
        download.setDescription(FieldUtils.getStringValue(component.getMetadata(), "description"));
        return download;
    }

    private YouTubeVideo createYouTubeVideo(org.dd4t.contentmodel.Component component) {
        YouTubeVideo youTubeVideo = new YouTubeVideo();
        fillMediaItemFields(component, youTubeVideo);
        youTubeVideo.setYouTubeId(FieldUtils.getStringValue(component.getMetadata(), "youTubeId"));
        return youTubeVideo;
    }

    private EclItem createEclItem(org.dd4t.contentmodel.Component component) {
        EclItem eclItem = new EclItem();
        fillMediaItemFields(component, eclItem);
        eclItem.setId(component.getId().split("-")[1]);
        eclItem.setEclUrl(component.getTitle().replace("ecl:0", "ecl:" + TcmUtils.getPublicationId(component.getId())));
        return eclItem;
    }

    private void fillMediaItemFields(org.dd4t.contentmodel.Component component, MediaItem mediaItem) {
        final Multimedia multimedia = component.getMultimedia();
        mediaItem.setUrl(multimedia.getUrl());
        mediaItem.setFileName(multimedia.getFileName());
        mediaItem.setFileSize(multimedia.getSize());
        mediaItem.setMimeType(multimedia.getMimeType());
    }
}
