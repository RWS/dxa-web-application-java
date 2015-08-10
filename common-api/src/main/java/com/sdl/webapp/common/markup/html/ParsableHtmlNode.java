package com.sdl.webapp.common.markup.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.List;

/**
 * ParsableHtmlNode
 *
 * @author nic
 */
public class ParsableHtmlNode extends HtmlNode {

    private String htmlText;
    private Element html = null;

    public ParsableHtmlNode(String htmlText) {
        this.htmlText = htmlText;
    }

    // TODO: Wrap JSoup API totally here...???

    public Element getHtmlElement() {
        if ( this.html == null ) {
            Element htmlElement = null;
            Document doc = Jsoup.parse(this.htmlText);
            List<Node> htmlNodes = doc.childNodes();
            Node firstNode = htmlNodes.get(0);
            if (firstNode instanceof Element) {
                Element element = (Element) firstNode;
                Elements elements = element.select("body");
                if (elements.size() > 0) {
                    if (elements.first().children().size() == 1) {
                        htmlElement = elements.first().child(0);
                    }
                }
                if (htmlElement == null) {
                    htmlElement = doc.child(0);
                }
            }
            this.html = htmlElement;
        }
        return this.html;
    }

    @Override
    protected String renderHtml() {
        if ( this.html != null ) {
            return this.html.outerHtml();
        }
        else {
            return this.htmlText;
        }
    }
}
