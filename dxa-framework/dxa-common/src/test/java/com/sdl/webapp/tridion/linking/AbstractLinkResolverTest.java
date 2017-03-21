package com.sdl.webapp.tridion.linking;

import org.junit.Test;
import org.springframework.context.annotation.Profile;

import java.util.Optional;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class AbstractLinkResolverTest {

    private TestLinkResolver linkResolver = new TestLinkResolver();

    @Test
    public void shouldReturnUrlItself_IfNotTcmUri() {
        //given 

        //when
        String index = linkResolver.resolveLink("/index", "1");
        String indexHtml = linkResolver.resolveLink("/index.html", "1");
        String page = linkResolver.resolveLink("/index/page.html", "1");
        String indexHtmlInner = linkResolver.resolveLink("/index/index.html", "1");

        //then
        assertEquals("/", index);
        assertEquals("/", indexHtml);
        assertEquals("/index/page", page);
        assertEquals("/index", indexHtmlInner);
    }

    @Test
    public void shouldGetPublicationIdFromTcmUri_IfPublicationIdIsNullOrEmpty() {
        //given 

        //when
        String link = linkResolver.resolveLink("tcm:2-3", null);
        String linkBinary = linkResolver.resolveLink("tcm:2-3", "", true);
        String linkPage = linkResolver.resolveLink("tcm:2-3-64", "");

        //then
        assertEquals("resolved-component-2", link);
        assertEquals("resolved-binary-2", linkBinary);
        assertEquals("resolved-page-2", linkPage);
    }

    @Profile("test")
    private static class TestLinkResolver extends AbstractLinkResolver {

        @Override
        protected Function<ResolvingData, Optional<String>> _binaryResolver() {
            return resolvingData -> Optional.of("resolved-binary-" + resolvingData.getPublicationId());
        }

        @Override
        protected Function<ResolvingData, Optional<String>> _componentResolver() {
            return resolvingData -> Optional.of("resolved-component-" + resolvingData.getPublicationId());
        }

        @Override
        protected Function<ResolvingData, Optional<String>> _pageResolver() {
            return resolvingData -> Optional.of("resolved-page-" + resolvingData.getPublicationId());
        }
    }
}