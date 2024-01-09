package com.sdl.webapp.tridion.xpm.markup;

import com.sdl.webapp.common.api.xpm.XpmRegionConfig;
import com.sdl.webapp.common.markup.MarkupDecoratorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

/**
 * XpmMarkupInitializer
 *
 * @author nic
 */
@Component
public class XpmMarkupInitializer {

    @Autowired
    private MarkupDecoratorRegistry registry;

    @Autowired
    private XpmRegionConfig xpmRegionConfig;

    /**
     * <p>initializeMarkup.</p>
     */
    @PostConstruct
    public void initializeMarkup() {

        RegionXpmMarkup regionXpmMarkup = new RegionXpmMarkup(xpmRegionConfig);
        EntityXpmMarkup entityXpmMarkup = new EntityXpmMarkup();

        this.registry.registerDecorator("Region", regionXpmMarkup);
        this.registry.registerDecorator("Regions", regionXpmMarkup);
        this.registry.registerDecorator("Entity", entityXpmMarkup);
        this.registry.registerDecorator("Entities", entityXpmMarkup);
    }
}
