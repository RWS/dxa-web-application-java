package com.sdl.webapp.common.markup.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import java.util.List;

public class ParsableHtmlNode extends HtmlNode {

    private String htmlText;
    private Element html = null;

    /**
     * <p>Constructor for ParsableHtmlNode.</p>
     *
     * @param htmlText a {@link java.lang.String} object.
     */
    public ParsableHtmlNode(String htmlText) {
        this.htmlText = htmlText;
    }

    // TODO: Wrap JSoup API totally here...???

    /**
     * <p>getHtmlElement.</p>
     *
     * @return a {@link org.jsoup.nodes.Element} object.
     */
    public Element getHtmlElement() {
        if (this.html == null) {
            Element htmlElement = null;
            Document doc = Jsoup.parse(this.htmlText);
            // The following settings ensure that Jsoup keeps self-enclosing tags unchanged
            doc.outputSettings().syntax(Document.OutputSettings.Syntax.xml);
            doc.outputSettings().escapeMode(Entities.EscapeMode.xhtml);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public String renderHtml() {
        if (this.html != null) {
            return this.html.outerHtml();
        } else {
            return this.htmlText;
        }
    }
}
