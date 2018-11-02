package com.sdl.webapp.tridion;

import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.enums.ContentNamespace;
import com.sdl.web.pca.client.contentmodel.generated.PublicationMapping;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import com.sdl.webapp.common.api.localization.LocalizationFactoryException;
import com.sdl.webapp.common.api.localization.LocalizationResolverException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLLocalizationResolverTest {

    @InjectMocks
    private GraphQLLocalizationResolver pcaLocalizationResolver = new GraphQLLocalizationResolver();

    @Mock
    private ApiClientProvider apiClientProvider;

    @Mock
    private ApiClient publicContentApi;

    @Mock
    PublicationMapping publicationMapping;

    @Mock
    LocalizationFactory localizationFactory;

    @Mock
    Localization localization;

    @Before
    public void setup(){
        when(apiClientProvider.getClient()).thenReturn(publicContentApi);
    }

    @Test
    public void getLocalization() throws LocalizationFactoryException, LocalizationResolverException {

        String testUrl ="http://localhost:8882/";

        when(localizationFactory.createLocalization("5","/")).thenReturn(localization);

        when(publicationMapping.getPublicationId()).thenReturn(5);
        when(publicationMapping.getPath()).thenReturn("/");
        when(apiClientProvider.getClient().getPublicationMapping(ContentNamespace.Sites,testUrl)).thenReturn(publicationMapping);

        Assert.assertNotNull(pcaLocalizationResolver.getLocalization(testUrl));
    }
}
