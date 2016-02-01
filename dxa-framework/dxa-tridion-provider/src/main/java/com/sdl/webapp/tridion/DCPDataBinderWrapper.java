package com.sdl.webapp.tridion;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.ComponentTemplate;
import org.dd4t.contentmodel.Page;
import org.dd4t.core.databind.BaseViewModel;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.SerializationException;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

/**
 * <p>DCPDataBinderWrapper class.</p>
 *
 * @author azarakovskiy
 * @version 1.3-SNAPSHOT
 */
public class DCPDataBinderWrapper implements DataBinder {

    private DataBinder dataBinder;

    /**
     * <p>Setter for the field <code>dataBinder</code>.</p>
     *
     * @param dataBinder a {@link org.dd4t.core.databind.DataBinder} object.
     */
    public void setDataBinder(DataBinder dataBinder) {
        this.dataBinder = dataBinder;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T extends Component> T buildComponent(Object o, Class<T> aClass) throws SerializationException {
        return this.dataBinder.buildComponent(o, aClass);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, BaseViewModel> buildModels(Object o, Set<String> set, String s) throws SerializationException {
        return this.dataBinder.buildModels(o, set, s);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends BaseViewModel> T buildModel(Object o, String s, String s1) throws SerializationException {
        return this.dataBinder.buildModel(o, s, s1);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends BaseViewModel> T buildModel(Object o, Class aClass, String s) throws SerializationException {
        return this.dataBinder.buildModel(o, aClass, s);
    }

    /** {@inheritDoc} */
    @Override
    public <T extends Page> T buildPage(String s, Class<T> aClass) throws SerializationException {
        return this.dataBinder.buildPage(s, aClass);
    }

    /** {@inheritDoc} */
    @Override
    public ComponentPresentation buildDynamicComponentPresentation(ComponentPresentation componentPresentation, Class<? extends Component> aClass) throws SerializationException {
        return componentPresentation;
    }

    /** {@inheritDoc} */
    @Override
    public String findComponentTemplateViewName(ComponentTemplate componentTemplate) throws IOException {
        return this.dataBinder.findComponentTemplateViewName(componentTemplate);
    }

    /** {@inheritDoc} */
    @Override
    public String getRootElementName(Object o) {
        return this.dataBinder.getRootElementName(o);
    }

    /** {@inheritDoc} */
    @Override
    public boolean renderDefaultComponentModelsOnly() {
        return this.dataBinder.renderDefaultComponentModelsOnly();
    }

    /** {@inheritDoc} */
    @Override
    public boolean renderDefaultComponentsIfNoModelFound() {
        return this.dataBinder.renderDefaultComponentsIfNoModelFound();
    }
}
