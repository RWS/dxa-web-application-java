package com.sdl.webapp.config;

import com.sdl.webapp.tridion.DCPDataBinderWrapper;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.contentmodel.impl.ComponentTemplateImpl;
import org.dd4t.core.serializers.impl.SerializerFactory;
import org.dd4t.core.serializers.impl.json.JSONSerializer;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.databind.builder.json.JsonModelConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
/**
 * <p>TridionSpringConfig class.</p>
 */
public class TridionSpringConfig {

    /**
     * <p>serializer.</p>
     *
     * @return a {@link org.dd4t.core.serializers.impl.json.JSONSerializer} object.
     */
    @Bean
    public JSONSerializer serializer() {
        return new JSONSerializer();
    }

    /**
     * <p>serializerFactory.</p>
     *
     * @return a {@link org.dd4t.core.serializers.impl.SerializerFactory} object.
     */
    @Bean
    public SerializerFactory serializerFactory() {
        return new SerializerFactory(serializer());
    }


    /**
     * <p>modelConverter.</p>
     *
     * @return a {@link org.dd4t.databind.builder.json.JsonModelConverter} object.
     */
    @Bean
    public JsonModelConverter modelConverter() {
        return new JsonModelConverter();
    }

    /**
     * <p>dataBinder.</p>
     *
     * @return a {@link org.dd4t.databind.builder.json.JsonDataBinder} object.
     */
    @Bean
    public JsonDataBinder dataBinder() {
        JsonDataBinder dataBinder = JsonDataBinder.getInstance();
        dataBinder.setRenderDefaultComponentModelsOnly(true);
        dataBinder.setRenderDefaultComponentsIfNoModelFound(true);
        dataBinder.setConverter(modelConverter());
        dataBinder.setConcreteComponentPresentationImpl(ComponentPresentationImpl.class);
        dataBinder.setConcreteComponentTemplateImpl(ComponentTemplateImpl.class);
        dataBinder.setConcreteComponentImpl(ComponentImpl.class);
        dataBinder.setConcreteFieldImpl(BaseField.class);
        return dataBinder;
    }

    /**
     * <p>dcpDataBinderWrapper.</p>
     *
     * @return a {@link com.sdl.webapp.tridion.DCPDataBinderWrapper} object.
     */
    @Bean
    public DCPDataBinderWrapper dcpDataBinderWrapper() {
        DCPDataBinderWrapper binderWrapper = new DCPDataBinderWrapper();
        binderWrapper.setDataBinder(dataBinder());
        return binderWrapper;
    }

    /**
     * <p>databindFactory.</p>
     *
     * @return a {@link org.dd4t.databind.DataBindFactory} object.
     */
    @Bean
    public DataBindFactory databindFactory() {
        DataBindFactory bindFactory = DataBindFactory.getInstance();
        bindFactory.setDataBinder(dcpDataBinderWrapper());
        return bindFactory;
    }
}
