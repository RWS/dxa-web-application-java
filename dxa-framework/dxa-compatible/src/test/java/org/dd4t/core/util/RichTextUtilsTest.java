package org.dd4t.core.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

/**
 * dd4t-parent
 *
 * @author R. Kempees
 */

public class RichTextUtilsTest {

    private static final String XHTMLBODYROOT = "xhtmlbodyroot";
    @Test
    public void resolveXhtmlBody() throws Exception {

       String html="<div><p>dsdsds<b><em>test</em></b></p></div>";

        Document document = Jsoup.parseBodyFragment("<" + XHTMLBODYROOT + ">" + html + "</" + XHTMLBODYROOT + ">");

        Element xhtmlBodyRoot = document.getElementsByTag(XHTMLBODYROOT).first();

        System.out.println(xhtmlBodyRoot.html());

        Document nwDocument = Jsoup.parseBodyFragment("<" + XHTMLBODYROOT + ">" + html + "</" + XHTMLBODYROOT + ">");
        nwDocument.outputSettings().indentAmount(0).prettyPrint(false);

        Element nwXhtmlBodyRoot = nwDocument.getElementsByTag(XHTMLBODYROOT).first();

        System.out.println(nwXhtmlBodyRoot.html());
    }


}
