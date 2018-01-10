package org.dd4t.thymeleaf.dialect.processor.xpm;

import org.dd4t.core.services.PropertiesService;
import org.dd4t.databind.viewmodel.base.TridionViewModelBase;
import org.dd4t.mvc.utils.XPMRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.IEngineConfiguration;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.engine.TemplateData;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.AbstractElementTagProcessor;
import org.thymeleaf.processor.element.IElementTagStructureHandler;
import org.thymeleaf.standard.expression.IStandardExpression;
import org.thymeleaf.standard.expression.IStandardExpressionParser;
import org.thymeleaf.standard.expression.StandardExpressions;
import org.thymeleaf.templatemode.TemplateMode;

import java.util.List;

/**
 * Custom processor that generates the Experience Manager tags on the page level
 * @author Quirijn Slings
 */

public class XpmFieldProcessor extends AbstractElementTagProcessor {
    private static final Logger LOG = LoggerFactory.getLogger(XpmFieldProcessor.class.getName());
    private static final String TAG_NAME = "field";
    private static final String SRC_ATTR_NAME = "src";
    private static final String FIELD_ATTR_NAME = "fieldname";
    private static final String INDEX_ATTR_NAME = "index";
    private static final int PRECEDENCE = 10000;
    
    public XpmFieldProcessor(final String dialectPrefix, PropertiesService propertiesService) {
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
            LOG.warn("xpm:field is used without a src attribute");
            return;
        }
        // check if there is a 'field' attribute on the current tag
        if (! tag.hasAttribute(FIELD_ATTR_NAME)) {
            LOG.warn("xpm:field is used without a fieldname attribute");
            return;
        }

        // retrieve the entity object from the attribute
        IStandardExpression expressionComponentPresentation = parser.parseExpression(context, tag.getAttributeValue(SRC_ATTR_NAME));
        TridionViewModelBase entity = (TridionViewModelBase) expressionComponentPresentation.execute(context);

        // retrieve the field name from the attribute
        IStandardExpression expressionFieldname = parser.parseExpression(context, tag.getAttributeValue(FIELD_ATTR_NAME));
        String fieldName = (String) expressionFieldname.execute(context);

        int index = 0;
        if (tag.hasAttribute(INDEX_ATTR_NAME)) {
            IStandardExpression expressionIndex = parser.parseExpression(context, tag.getAttributeValue(INDEX_ATTR_NAME));
            index = (int) expressionIndex.execute(context);
        }
        
        // get an XPM renderer (part of DD4T) and generate the XPM comment for this page
        XPMRenderer renderer = XPMRenderer.getInstance();
        try {
	        String xpmMarkup = renderer.componentField(entity.getXPath(fieldName), entity.isMultiValued(fieldName), index);
	        
	        // create a model with the returned markup
	        final IModelFactory modelFactory = context.getModelFactory();
	        final IModel model = modelFactory.parse(context.getTemplateData(), xpmMarkup);
	
	        // instruct the engine to replace this entire element with the specified model
	        structureHandler.replaceWith(model, false);
        } 
        catch(IllegalArgumentException ex) {
            LOG.error(createMessage(context));
        }
    }

	private String createMessage(ITemplateContext context) {
		StringBuilder message = new StringBuilder();
		message.append("An IllegalArgumentException was thrown during template parsing ( template: ");
		List<TemplateData> list = context.getTemplateStack();
		if (list != null) {
			for (TemplateData data : list) {
				message.append(data.getTemplate()).append(" ");
			}
		}
		message.append(")");
		return message.toString();
	}
}