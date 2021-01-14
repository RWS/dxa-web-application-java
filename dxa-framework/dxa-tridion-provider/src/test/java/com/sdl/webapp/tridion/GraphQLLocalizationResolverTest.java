package com.sdl.webapp.tridion;

import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.generated.PublicationMapping;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GraphQLLocalizationResolverTest {

    @InjectMocks
    private GraphQLLocalizationResolver localizationResolver = new GraphQLLocalizationResolver();
    @Mock
    private ApiClientProvider apiClientProvider;
    @Mock
    private ApiClient publicContentApi;
    @Mock
    private PublicationMapping publicationMapping;
    @Mock
    private LocalizationFactory localizationFactory;
    @Mock
    private Localization localization;
    @Mock
    private Localization localization2;
    @Mock
    private Localization localization3;

    private String testUrl = "http://localhost:8882/";

    @Before
    public void setup() throws Exception {
        when(apiClientProvider.getClient()).thenReturn(publicContentApi);

        when(localization.getId()).thenReturn("5");
        when(localizationFactory.createLocalization("5","/verola")).thenReturn(localization);
        when(publicationMapping.getPublicationId()).thenReturn(5);
        when(publicationMapping.getPath()).thenReturn("/verola");
        when(apiClientProvider.getClient().getPublicationMapping(Sites, testUrl)).thenReturn(publicationMapping);
    }

    @Test
    public void getLocalization() throws Exception {
        assertSame(localization, localizationResolver.getLocalization(testUrl));
    }

    @Test
    public void testInitState() {
        assertTrue(localizationResolver.getAllLocalizations().isEmpty());
    }

    @Test
    public void refreshLocalization() throws Exception {
        createAndCache3Localizations();
        assertEquals(3, localizationResolver.getAllLocalizations().size());

        localizationResolver.refreshLocalization(localization);
        assertEquals(2, localizationResolver.getAllLocalizations().size());

        localizationResolver.refreshLocalization(localization2);
        assertEquals(1, localizationResolver.getAllLocalizations().size());

        localizationResolver.refreshLocalization(localization3);
        assertTrue(localizationResolver.getAllLocalizations().isEmpty());
    }

    private void createAndCache3Localizations() throws Exception {
        localizationResolver.getLocalization(testUrl); //create for pub "5" and url

        when(apiClientProvider.getClient().getPublicationMapping(Sites, testUrl + "one")).thenReturn(publicationMapping);
        when(publicationMapping.getPublicationId()).thenReturn(1);
        when(publicationMapping.getPath()).thenReturn("/one");
        when(localizationFactory.createLocalization("1","/one")).thenReturn(localization2);
        when(localization2.getId()).thenReturn("1");
        localizationResolver.getLocalization(testUrl + "one");

        when(apiClientProvider.getClient().getPublicationMapping(Sites, testUrl + "two")).thenReturn(publicationMapping);
        when(publicationMapping.getPublicationId()).thenReturn(2);
        when(publicationMapping.getPath()).thenReturn("/two");
        when(localizationFactory.createLocalization("2","/two")).thenReturn(localization3);
        when(localization3.getId()).thenReturn("2");
        localizationResolver.getLocalization(testUrl + "two");
    }
}
