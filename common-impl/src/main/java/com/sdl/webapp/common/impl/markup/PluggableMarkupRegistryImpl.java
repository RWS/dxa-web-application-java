package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.markup.PluggableMarkupRegistry;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * PluggableMarkupRegistryImpl
 *
 * @author nic
 */
@Component
public class PluggableMarkupRegistryImpl implements PluggableMarkupRegistry {

    private Map<String, List<HtmlNode>> markupDictionary = new HashMap<>();

    // TODO: Make sure that the markup is not repeated (for example js includes)

    @Override
    public List<HtmlNode> getPluggableMarkup(String label) {
        List<HtmlNode> markupList = markupDictionary.get(label);
        if ( markupList == null ) {
            markupList = Collections.EMPTY_LIST;
        }
        return markupList;
    }

    @Override
    public void registerPluggableMarkup(MarkupType markupType, HtmlNode markup) {
       this.registerPluggableMarkup(markupType.name().toLowerCase().replace("_", "-"), markup);
    }

    @Override
    public void registerPluggableMarkup(String label, HtmlNode markup) {
        List<HtmlNode> markupList = markupDictionary.get(label);
        if ( markupList == null ) {
            markupList = new ArrayList<>();
            markupDictionary.put(label, markupList);
        }
        markupList.add(markup);
    }
}
