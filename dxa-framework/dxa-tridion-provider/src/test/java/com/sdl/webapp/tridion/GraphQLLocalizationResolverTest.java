package com.sdl.webapp.tridion;

import com.sdl.dxa.tridion.pcaclient.ApiClientProvider;
import com.sdl.web.pca.client.ApiClient;
import com.sdl.web.pca.client.contentmodel.generated.PublicationMapping;
import com.sdl.webapp.common.api.localization.Localization;
import com.sdl.webapp.common.api.localization.LocalizationFactory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.sdl.web.pca.client.contentmodel.enums.ContentNamespace.Sites;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
    public void setup() throws Exception {
        lenient().when(apiClientProvider.getClient()).thenReturn(publicContentApi);

        lenient().when(localization.getId()).thenReturn("5");
        lenient().when(localizationFactory.createLocalization("5", "/verola")).thenReturn(localization);
        lenient().when(publicationMapping.getPublicationId()).thenReturn(5);
        lenient().when(publicationMapping.getPath()).thenReturn("/verola");
        lenient().when(apiClientProvider.getClient().getPublicationMapping(Sites, testUrl)).thenReturn(publicationMapping);
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
        createAndCacheNLocalizations(2, false);
        assertEquals(3, localizationResolver.getAllLocalizations().size());

        localizationResolver.refreshLocalization(localization);
        assertEquals(2, localizationResolver.getAllLocalizations().size());

        localizationResolver.refreshLocalization(localization2);
        assertTrue(localizationResolver.getAllLocalizations().isEmpty());
    }

    @Test
    public void refreshLocalizationWithSeveralUrlsForLocalization() throws Exception {
        createAndCacheNLocalizations(5, true);
        when(localization2.getId()).thenReturn("5");
        assertEquals(5, localizationResolver.getAllLocalizations().size());

        localizationResolver.refreshLocalization(localization2);
        assertEquals(4, localizationResolver.getAllLocalizations().size());
    }

    //creates and caches 1-9 localizations
    private void createAndCacheNLocalizations(int n, boolean useDifferentLocalizationa) throws Exception {
        String[] cypherWords = {"one", "two", "three", "four", "five", "six", "seven", "eight", "nine"};
        localizationResolver.getLocalization(testUrl); //create for pub "5" and url

        for (int i = 0; i <= n - 1; i++) {
            lenient().when(apiClientProvider.getClient().getPublicationMapping(Sites, testUrl + cypherWords[i]))
                    .thenReturn(publicationMapping);
            lenient().when(publicationMapping.getPublicationId()).thenReturn(i + 1);
            lenient().when(publicationMapping.getPath()).thenReturn("/" + cypherWords[i]);
            if (useDifferentLocalizationa) {
                Localization loc = mock(Localization.class);
                lenient().when(loc.getId()).thenReturn(String.valueOf(i + 1));
                lenient().when(localizationFactory.createLocalization(String.valueOf(i + 1), "/" + cypherWords[i]))
                        .thenReturn(loc);
            } else {
                lenient().when(localization2.getId()).thenReturn(String.valueOf(i + 1));
                lenient().when(localizationFactory.createLocalization(String.valueOf(i + 1), "/" + cypherWords[i]))
                        .thenReturn(localization2);
            }


            localizationResolver.getLocalization(testUrl + cypherWords[i]);
        }
    }
}
