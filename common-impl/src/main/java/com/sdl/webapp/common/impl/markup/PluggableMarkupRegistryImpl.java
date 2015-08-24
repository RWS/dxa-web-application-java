package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.api.ThreadLocalManager;
import com.sdl.webapp.common.markup.PluggableMarkupRegistry;
import com.sdl.webapp.common.markup.html.HtmlNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

/**
 * PluggableMarkupRegistryImpl
 *
 * @author nic
 */
@Component
public class PluggableMarkupRegistryImpl implements PluggableMarkupRegistry {

    // TODO: Make sure that the markup is not repeated (for example js includes)

    private Map<String, List<HtmlNode>> markupDictionary = new HashMap<>();

    private ThreadLocal<Map<String, List<HtmlNode>>> contextualMarkupDictionary = new ThreadLocal<>();

    @Autowired
    private ThreadLocalManager threadLocalManager;

    @PostConstruct
    public void initialize() {
        this.threadLocalManager.registerThreadLocal(contextualMarkupDictionary);
    }

    @Override
    public List<HtmlNode> getPluggableMarkup(String label) {

        List<HtmlNode> markupList = new ArrayList<>();

        List<HtmlNode> globalMarkupList = markupDictionary.get(label);
        if ( globalMarkupList != null ) {
            markupList.addAll(globalMarkupList);
        }
        Map<String, List<HtmlNode>> requestMarkupDictionary = contextualMarkupDictionary.get();
        if ( requestMarkupDictionary != null ) {
            List<HtmlNode> requestMarkupList = requestMarkupDictionary.get(label);
            if ( requestMarkupList != null ) {
                markupList.addAll(requestMarkupList);
            }
        }
        return markupList;
    }

    @Override
    public void registerPluggableMarkup(MarkupType markupType, HtmlNode markup) {
       this.registerPluggableMarkup(markupType.name().toLowerCase().replace("_", "-"), markup);
    }

    @Override
    public void registerPluggableMarkup(String label, HtmlNode markup) {
        registerPluggableMarkup(label, markup, this.markupDictionary);
    }

    @Override
    public void registerContextualPluggableMarkup(MarkupType markupType, HtmlNode markup) {
        this.registerContextualPluggableMarkup(markupType.name().toLowerCase(), markup);
    }

    @Override
    public void registerContextualPluggableMarkup(String label, HtmlNode markup) {
        Map<String, List<HtmlNode>> markupDictionary = contextualMarkupDictionary.get();
        if ( markupDictionary == null ) {
            markupDictionary = new HashMap<>();
            contextualMarkupDictionary.set(markupDictionary);
        }
        registerPluggableMarkup(label, markup, markupDictionary);
    }

    private void registerPluggableMarkup(String label, HtmlNode markup, Map<String, List<HtmlNode>> markupDictionary) {
        List<HtmlNode> markupList = markupDictionary.get(label);
        if ( markupList == null ) {
            markupList = new ArrayList<>();
            markupDictionary.put(label, markupList);
        }
        markupList.add(markup);
    }
}
