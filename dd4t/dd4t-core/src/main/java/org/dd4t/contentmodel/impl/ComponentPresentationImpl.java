package org.dd4t.contentmodel.impl;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.GenericComponent;

/**
 * Class representing a component presentation which holds a component template and a component.
 *
 * @author bjornl
 */
public class ComponentPresentationImpl implements ComponentPresentation {

    @JsonProperty("Component")
    @JsonDeserialize(as = ComponentImpl.class)
    private GenericComponent component;

    @JsonProperty("ComponentTemplate")
    @JsonDeserialize(as = ComponentTemplateImpl.class)
    private ComponentTemplate componentTemplate;

    @JsonProperty("IsDynamic")
    private boolean isDynamic;

    @JsonProperty("RenderedContent")
    private String renderedContent;

    @JsonProperty("OrderOnPage")
    private int orderOnPage;

    /**
     * Get the component
     *
     * @return the component
     */
    public GenericComponent getComponent() {
        return component;
    }

    /**
     * Set the component
     *
     * @param component
     */
    public void setComponent(GenericComponent component) {
        this.component = component;
    }

    /**
     * Get the component template
     *
     * @return the component template
     */
    public ComponentTemplate getComponentTemplate() {
        return componentTemplate;
    }

    /**
     * Set the component template
     *
     * @param componentTemplate
     */
    public void setComponentTemplate(ComponentTemplate componentTemplate) {
        this.componentTemplate = componentTemplate;
    }

    public String getRenderedContent() {
        return renderedContent;
    }

    public void setRenderedContent(String renderedContent) {
        this.renderedContent = renderedContent;
    }

    @Override
    public boolean isDynamic() {
        return isDynamic;
    }

    public void setIsDynamic(final boolean isDynamic) {
        this.isDynamic = isDynamic;
    }

    public int getOrderOnPage() {
        return orderOnPage;
    }

    public void setOrderOnPage(final int orderOnPage) {
        this.orderOnPage = orderOnPage;
    }
}
