package org.dd4t.thymeleaf.dialect;

import java.util.HashSet;
import java.util.Set;

import javax.annotation.Resource;

import org.dd4t.core.services.PropertiesService;
import org.dd4t.thymeleaf.dialect.processor.xpm.XpmComponentPresentationProcessor;
import org.dd4t.thymeleaf.dialect.processor.xpm.XpmFieldProcessor;
import org.dd4t.thymeleaf.dialect.processor.xpm.XpmPageInitProcessor;
import org.thymeleaf.dialect.IProcessorDialect;
import org.thymeleaf.processor.IProcessor;


public class XpmDialect implements IProcessorDialect {

    @Resource
    PropertiesService propertiesService;

    
    
    public XpmDialect() {
    }

    /**
     * Prefix of the dialect.
     */
    @Override
    public String getPrefix() {
        return "xpm";
    }

    /**
     * Available processors
     */
    @Override
    public Set<IProcessor> getProcessors(String dialectPrefix) {
        final Set<IProcessor> processors = new HashSet<IProcessor>();
        processors.add(new XpmPageInitProcessor(getPrefix(), propertiesService));
        processors.add(new XpmComponentPresentationProcessor(getPrefix(), propertiesService));
        processors.add(new XpmFieldProcessor(getPrefix(), propertiesService));
        return processors;
    }

    @Override
    public String getName() {
        return "XPM Dialect";
    }

    @Override
    public int getDialectProcessorPrecedence() {
        return 0;
    }

}