package com.sdl.webapp.common.api.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.mapping.semantic.annotations.SemanticMappingIgnore;
import com.sdl.webapp.common.api.model.AbstractViewModel;
import com.sdl.webapp.common.api.model.EntityModel;
import com.sdl.webapp.common.api.model.MvcData;
import com.sdl.webapp.common.api.model.RichTextFragment;
import com.sdl.webapp.common.api.serialization.json.annotation.JsonXpmAware;
import com.sdl.webapp.common.exceptions.DxaException;
import com.sdl.webapp.common.markup.html.HtmlElement;
import com.sdl.webapp.common.util.ApplicationContextHolder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * Abstract implementation of entity model. This is a basic extension point to create your models.
 * @dxa.publicApi
 */
@Data
@EqualsAndHashCode(callSuper = true)
@SemanticMappingIgnore
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public abstract class AbstractEntityModel extends AbstractViewModel implements EntityModel, RichTextFragment {

    @JsonProperty("Id")
    private String id;

    @JsonProperty("XpmPropertyMetadata")
    @JsonXpmAware
    private Map<String, String> xpmPropertyMetadata;

    public AbstractEntityModel(AbstractEntityModel other) {
        super(other);
        this.id = other.id;
        this.xpmPropertyMetadata = other.xpmPropertyMetadata;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SneakyThrows(JsonProcessingException.class)
    public String getXpmMarkup(Localization localization) {
        return isEmpty(getXpmMetadata()) ? "" : String.format("<!-- Start Component Presentation: %s -->",
                ApplicationContextHolder.getContext().getBean(ObjectMapper.class).writeValueAsString(getXpmMetadata()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public HtmlElement toHtmlElement() throws DxaException {
        throw new UnsupportedOperationException(
                String.format("Direct rendering of View Model type '%s' to HTML is not supported." +
                                " Consider using View Model property of type RichText in combination with DxaRichText() in view code to avoid direct rendering to HTML." +
                                " Alternatively, override method %s.toHtmlElement().",
                        getClass().getName(), getClass().getName())
        );
    }

    @Override
    public MvcData getMvcData() {
        return super.getMvcData() != null ? super.getMvcData() : getDefaultMvcData();
    }

    /**
     * Gets the default View for this Entity Model (if any).
     * <p>If this method is overridden in a subclass, it will be possible to render "embedded" Entity Models of that
     * type using the EntityTag.</p>
     *
     * @return default MvcData if any of {@code null}
     */
    @JsonIgnore
    @Nullable
    public MvcData getDefaultMvcData() {
        return null;
    }
}
