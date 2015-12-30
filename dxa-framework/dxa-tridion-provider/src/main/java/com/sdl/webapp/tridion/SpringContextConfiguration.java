package com.sdl.webapp.tridion;

import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.contentmodel.impl.ComponentTemplateImpl;
import org.dd4t.core.serializers.impl.SerializerFactory;
import org.dd4t.core.serializers.impl.json.JSONSerializer;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.databind.builder.json.JsonModelConverter;
import org.dd4t.providers.impl.BrokerLinkProvider;
import org.dd4t.providers.impl.BrokerPageProvider;
import org.dd4t.providers.impl.BrokerTaxonomyProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SpringContextConfiguration {
//    DD4T beans
    @Bean
    public BrokerLinkProvider linkProvider() {
        BrokerLinkProvider linkProvider = new BrokerLinkProvider();
        linkProvider.setContentIsCompressed("false");
        return linkProvider;
    }
    @Bean
    public BrokerPageProvider pageProvider() {
        BrokerPageProvider pageProvider = new BrokerPageProvider();
        pageProvider.setContentIsCompressed("false");
        return pageProvider;
    }

    @Bean
    public JSONSerializer serializer() {
        return new JSONSerializer();
    }

    @Bean
    public SerializerFactory serializerFactory() {
        return new SerializerFactory(serializer());
    }

    @Bean
    public BrokerTaxonomyProvider taxonomyProvider() {
        return new BrokerTaxonomyProvider();
    }

    @Bean
    public JsonModelConverter modelConverter() {
        return new JsonModelConverter();
    }

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

    @Bean
    public DCPDataBinderWrapper dcpDataBinderWrapper() {
        DCPDataBinderWrapper binderWrapper = new DCPDataBinderWrapper();
        binderWrapper.setDataBinder(dataBinder());
        return binderWrapper;
    }

    @Bean
    public DataBindFactory databindFactory() {
        DataBindFactory bindFactory = DataBindFactory.getInstance();
        bindFactory.setDataBinder(dcpDataBinderWrapper());
        return bindFactory;
    }
}
