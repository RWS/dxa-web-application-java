package com.sdl.webapp.common.api.model.entity;

import com.google.common.base.Strings;
import com.sdl.webapp.common.api.mapping.annotations.SemanticEntity;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperties;
import com.sdl.webapp.common.api.mapping.annotations.SemanticProperty;

import static com.sdl.webapp.common.api.mapping.config.SemanticVocabulary.SCHEMA_ORG;

@SemanticEntity(entityName = "MediaObject", vocabulary = SCHEMA_ORG, prefix = "s")
public class Download extends MediaItem {

    @SemanticProperties({
            @SemanticProperty("s:name"),
            @SemanticProperty("s:description")
    })
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Download{" +
                "url='" + getUrl() + '\'' +
                ", fileName='" + getFileName() + '\'' +
                ", fileSize=" + getFileSize() +
                ", mimeType='" + getMimeType() + '\'' +
                ", description='" + description + '\'' +
                '}';
    }

	@Override
	public String toHtml(String widthFactor) {
		return toHtml(widthFactor, 0,"",0);
	}

	@Override
	public String toHtml(String widthFactor, double aspect , String cssClass , int containerSize )
    {
        String descriptionHtml = Strings.isNullOrEmpty(getDescription()) ? null : String.format("<small>{0}</small>", getDescription());
        String s = new StringBuilder()
            .append("<div class=\"download-list\">")
            .append(String.format("<i class=\"fa {0}\"></i>", this.getIconClass()))
            .append("<div>")
            .append(String.format("<a href=\"{0}\">{1}</a> <small class=\"size\">({2})</small>", this.getUrl(), this.getFileName(), this.getFriendlyFileSize()))
            .append(String.format("{4}", this.getDescription()))
            .append("</div>")
            .append("</div>").toString();
		 return s;
    }
}
