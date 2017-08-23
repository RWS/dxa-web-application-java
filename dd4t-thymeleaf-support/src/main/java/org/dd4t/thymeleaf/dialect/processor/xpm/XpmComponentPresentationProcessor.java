package org.dd4t.thymeleaf.dialect.processor.xpm;

import org.dd4t.core.services.PropertiesService;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;
import org.dd4t.mvc.utils.XPMRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

/**
 * Custom processor that generates the Experience Manager tags on the page level
 * @author Quirijn Slings
 */

public class XpmComponentPresentationProcessor extends AbstractElementTagProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(XpmComponentPresentationProcessor.class);
    private static final String TAG_NAME = "componentpresentation";
    private static final String SRC_ATTR_NAME = "src";
    private static final int PRECEDENCE = 10000;
    private static final String XPM_ENABLED = "xpm.enabled";

    public XpmComponentPresentationProcessor(final String dialectPrefix, PropertiesService propertiesService) {
     super(TemplateMode.HTML,
             dialectPrefix,
             TAG_NAME,
             true,
             null,
             false,
             PRECEDENCE
             );
         String xpmEnabledAsString = propertiesService.getProperty(XPM_ENABLED);
         if (xpmEnabledAsString != null) {
             XPMRenderer.getInstance().setEnabled(Boolean.parseBoolean(xpmEnabledAsString));
         }
    }
    
    /**
     * Process the tag
     */
    
    @Override
    protected void doProcess(
            final ITemplateContext context, final IProcessableElementTag tag,
            final IElementTagStructureHandler structureHandler) {
        final IEngineConfiguration configuration = context.getConfiguration();
        final IStandardExpressionParser parser = StandardExpressions.getExpressionParser(configuration);
        
        // check if there is a 'src' attribute on the current tag
        if (! tag.hasAttribute(SRC_ATTR_NAME)) {
            LOG.warn("Cannot find the {} attribute on this tag!", SRC_ATTR_NAME);
            return;
        }

        // retrieve the entity object from the attribute
        final IStandardExpression expressionComponentPresentation = parser.parseExpression(context, tag.getAttributeValue(SRC_ATTR_NAME));
        final TridionViewModelBase entity = (TridionViewModelBase) expressionComponentPresentation.execute(context);
        
        // get an XPM renderer (part of DD4T) and generate the XPM comment for this page
        final XPMRenderer renderer = XPMRenderer.getInstance();
        final String xpmMarkup = renderer.componentPresentation(entity.getTcmUri().toString(), entity.getLastModified(), entity.getTemplateUri().toString(), false);
        
        // create a model with the returned markup
        final IModelFactory modelFactory = context.getModelFactory();
        final IModel model = modelFactory.parse(context.getTemplateData(), xpmMarkup);

        // instruct the engine to replace this entire element with the specified model
        structureHandler.replaceWith(model, false);
    }
}