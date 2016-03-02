package com.sdl.webapp.config;

import com.sdl.webapp.tridion.DCPDataBinderWrapper;
import org.dd4t.contentmodel.impl.BaseField;
import org.dd4t.contentmodel.impl.ComponentImpl;
import org.dd4t.contentmodel.impl.ComponentPresentationImpl;
import org.dd4t.contentmodel.impl.ComponentTemplateImpl;
import org.dd4t.core.factories.impl.PageFactoryImpl;
import org.dd4t.core.factories.impl.TaxonomyFactoryImpl;
import org.dd4t.core.processors.impl.RichTextWithLinksResolver;
import org.dd4t.core.providers.EHCacheProvider;
import org.dd4t.core.resolvers.impl.DefaultLinkResolver;
import org.dd4t.core.serializers.impl.SerializerFactory;
import org.dd4t.core.serializers.impl.json.JSONSerializer;
import org.dd4t.databind.DataBindFactory;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.dd4t.databind.builder.json.JsonModelConverter;
import org.dd4t.providers.LinkProvider;
import org.dd4t.providers.PageProvider;
import org.dd4t.providers.PayloadCacheProvider;
import org.dd4t.providers.TaxonomyProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.sdl.webapp.tridion")
//todo dxa2 move to com.sdl.dxa
public class TridionSpringConfig {

    @Autowired
    public PayloadCacheProvider cacheProvider;

    @Autowired
    public TaxonomyProvider taxonomyProvider;

    @Autowired
    public PageProvider pageProvider;

    @Autowired
    public LinkProvider linkProvider;

    @Bean
    public PayloadCacheProvider cacheProvider() {
        EHCacheProvider cacheProvider = new EHCacheProvider();
        cacheProvider.setCheckForPreview(true);
        return cacheProvider;
    }

    @Bean
    public TaxonomyFactoryImpl taxonomyFactory() {
        TaxonomyFactoryImpl taxonomyFactory = TaxonomyFactoryImpl.getInstance();
        taxonomyFactory.setCacheProvider(cacheProvider);
        taxonomyFactory.setTaxonomyProvider(taxonomyProvider);
        return taxonomyFactory;
    }

    @Bean
    public PageFactoryImpl pageFactory() {
        PageFactoryImpl pageFactory = PageFactoryImpl.getInstance();
        pageFactory.setCacheProvider(cacheProvider);
        pageFactory.setPageProvider(pageProvider);
        return pageFactory;
    }

    @Bean
    public DefaultLinkResolver linkResolver() {
        DefaultLinkResolver linkResolver = new DefaultLinkResolver();
        linkResolver.setContextPath("");
        linkResolver.setLinkProvider(linkProvider);
        return linkResolver;
    }

    @Bean
    public RichTextWithLinksResolver richTextWithLinksResolver() {
        RichTextWithLinksResolver richTextWithLinksResolver = new RichTextWithLinksResolver();
        richTextWithLinksResolver.setLinkResolver(linkResolver());
        return richTextWithLinksResolver;
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
