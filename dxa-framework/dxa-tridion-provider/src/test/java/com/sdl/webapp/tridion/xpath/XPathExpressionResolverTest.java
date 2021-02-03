package com.sdl.webapp.tridion.xpath;

import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class XPathExpressionResolverTest {

    @Test
    public void testLinksExpr() throws Exception {
        Assert.assertNotNull(XPathExpressionResolver.XPATH_LINKS.expr());
    }

    @Test
    public void testImagesExpr() throws Exception {
        Assert.assertNotNull(XPathExpressionResolver.XPATH_IMAGES.expr());
    }

    @Test
    public void testYouTubeExpr() throws Exception {
        Assert.assertNotNull(XPathExpressionResolver.XPATH_YOUTUBE.expr());
    }

}