package org.dd4t.thymeleaf.dialect.processor.xpm;

import java.util.logging.Logger;

import org.dd4t.contentmodel.Page;
import org.dd4t.core.services.PropertiesService;
import org.dd4t.mvc.utils.XPMRenderer;
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

public class XpmPageInitProcessor extends AbstractElementTagProcessor {
    private static final Logger LOG = Logger.getLogger(XpmPageInitProcessor.class.getName());
    private static final String TAG_NAME = "page";
    private static final String SRC_ATTR_NAME = "src";
    private static final int PRECEDENCE = 10000;
    
    public XpmPageInitProcessor(final String dialectPrefix, PropertiesService propertiesService) {
     super(TemplateMode.HTML,
             dialectPrefix,
             TAG_NAME,
             true,
             null,
             false,
             PRECEDENCE
             );
         String xpmEnabledAsString = propertiesService.getProperty("xpm.enabled");
         if (xpmEnabledAsString != null) {
             XPMRenderer.getInstance().setEnabled(Boolean.parseBoolean(xpmEnabledAsString));
         }
         XPMRenderer.getInstance().setCmsUrl(propertiesService.getProperty("xpm.cmsUrl"));
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
            // TODO: log a warning message
            return;
        }

        // retrieve the DD4T Page object from the attribute
        IStandardExpression expressionPage = parser.parseExpression(context, tag.getAttributeValue(SRC_ATTR_NAME));
        Page page = (Page) expressionPage.execute(context);

        // get an XPM renderer (part of DD4T) and generate the XPM comment for this page
        XPMRenderer renderer = XPMRenderer.getInstance();
        String xpmMarkup = renderer.initPage(page.getId(), page.getRevisionDate(), page.getPageTemplate().getId());
        
        // create a model with the returned markup
        final IModelFactory modelFactory = context.getModelFactory();
        final IModel model = modelFactory.parse(context.getTemplateData(), xpmMarkup);

        // instruct the engine to replace this entire element with the specified model
        structureHandler.replaceWith(model, false);
    }
}