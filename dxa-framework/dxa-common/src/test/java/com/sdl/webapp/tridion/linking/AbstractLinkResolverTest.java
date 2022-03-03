package com.sdl.webapp.tridion.linking;

import org.junit.Test;
import org.springframework.context.annotation.Profile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.Assert.assertEquals;

public class AbstractLinkResolverTest {

    @Test
    public void shouldReturnUrlItself_IfNotTcmUri() {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver();

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
        TestLinkResolver linkResolver = new TestLinkResolver();

        //when
        String link = linkResolver.resolveLink("tcm:2-3", null);
        String linkBinary = linkResolver.resolveLink("tcm:2-3", "", true);
        String linkPage = linkResolver.resolveLink("tcm:2-3-64", "");

        //then
        assertEquals("resolved-component-2", link);
        assertEquals("resolved-binary-2", linkBinary);
        assertEquals("resolved-page-2", linkPage);
    }

    @Test
    public void shouldRemoveExtension() {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(true, true, false);

        //when
        String pageHtml = linkResolver.resolveLink("/page.html", "1");

        //then
        assertEquals("/page", pageHtml);
    }

    @Test
    public void shouldNotRemoveExtension_IfDisabled() {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(false, true, false);

        //when
        String pageHtml = linkResolver.resolveLink("/page.html", "1");

        //then
        assertEquals("/page.html", pageHtml);
    }

    @Test
    public void shouldRemoveIndex() {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(true, true, false);

        //when
        String indexHtml = linkResolver.resolveLink("/index", "1");

        //then
        assertEquals("/", indexHtml);
    }

    @Test
    public void shouldNotRemoveIndex_IfDisabled() {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(true, false, false);

        //when
        String indexHtml = linkResolver.resolveLink("/index", "1");

        //then
        assertEquals("/index", indexHtml);
    }

    @Test
    public void shouldNotRemoveIndex_IfExtensionIsNotRemoved() {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(false, true, false);

        //when
        String indexHtml = linkResolver.resolveLink("/index.html", "1");

        //then
        assertEquals("/index.html", indexHtml);
    }

    @Test
    public void shouldNotRemoveIndexOrExtension_IfDisabled() throws Exception {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(false, false, false);

        //when
        String indexHtml = linkResolver.resolveLink("/index.html", "1");

        //then
        assertEquals("/index.html", indexHtml);
    }

    @Test
    public void shouldKeepIndexTrailingSlash() throws Exception {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(true, true, true);

        //when
        String indexHtml = linkResolver.resolveLink("/branch/index.html", "1");

        //then
        assertEquals("/branch/", indexHtml);
    }

    @Test
    public void shouldNotAddIndexTrailingSlash() throws Exception {
        //given
        TestLinkResolver linkResolver = new TestLinkResolver(true, true, true);

        //when
        String indexHtml = linkResolver.resolveLink("/index.html", "1");

        //then
        assertEquals("/", indexHtml);
    }

    @Profile("test")
    private static class TestLinkResolver extends AbstractLinkResolver {

        public TestLinkResolver() {
            ReflectionTestUtils.setField(this, "shouldRemoveExtension", true);
            ReflectionTestUtils.setField(this, "shouldStripIndexPath", true);
            ReflectionTestUtils.setField(this, "shouldKeepTrailingSlash", false);
        }

        public TestLinkResolver(boolean shouldRemoveExtension, boolean shouldStripIndexPath,
                                boolean shouldKeepTrailingSlash) {
            ReflectionTestUtils.setField(this, "shouldRemoveExtension", shouldRemoveExtension);
            ReflectionTestUtils.setField(this, "shouldStripIndexPath", shouldStripIndexPath);
            ReflectionTestUtils.setField(this, "shouldKeepTrailingSlash", shouldKeepTrailingSlash);
        }

        @Override
        protected String resolveComponent(ResolvingData resolvingData) {
            return "resolved-component-" + resolvingData.getPublicationId();
        }

        @Override
        protected String resolvePage(ResolvingData resolvingData) {
            return "resolved-page-" + resolvingData.getPublicationId();
        }

        @Override
        protected String resolveBinary(ResolvingData resolvingData) {
            return "resolved-binary-" + resolvingData.getPublicationId();
        }
    }
}
