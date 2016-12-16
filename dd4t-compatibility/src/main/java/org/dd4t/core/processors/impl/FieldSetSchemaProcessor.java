package org.dd4t.core.processors.impl;

import java.util.Collection;

import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.Field;
import org.dd4t.contentmodel.FieldSet;
import org.dd4t.contentmodel.FieldType;
import org.dd4t.contentmodel.Item;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.Schema;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.core.exceptions.ProcessorException;
import org.dd4t.core.processors.Processor;
import org.dd4t.core.processors.RunPhase;
import org.dd4t.core.request.RequestContext;

/**
 * Processor implements backwards compatibility for the dd4t-1 schema location on the fieldset. 
 * It crawls all fieldsets passed in, and moves the object around.
 * 
 * @author Rogier Oudshoorn
 *
 */
public class FieldSetSchemaProcessor extends BaseProcessor implements Processor {

	@Override
	public void execute(Item item, RequestContext requestContext)
			throws ProcessorException {
		
		if(item instanceof Component){
			Component comp = (Component) item;
			processFieldCollection(comp.getContent().values());
			processFieldCollection(comp.getMetadata().values());
			
		}

		if(item instanceof Page){
			Page page = (Page) item;
			processFieldCollection(page.getMetadata().values());
		}
	}
		
	private void processFields(Schema schema, FieldSet fieldSet){
		fieldSet.setSchema(schema);
		
		processFieldCollection(fieldSet.getContent().values());
	}
	
	private void processFieldCollection(Collection<Field> fields){
		for(Field field : fields){
			if(field.getFieldType().equals(FieldType.EMBEDDED)){
				EmbeddedField embField = (EmbeddedField) field;
				for(FieldSet subset : embField.getEmbeddedValues()){
					processFields(embField.getEmbeddedSchema(), subset );
				}
			}
		}
	}
	
	/**
	 * This feature only needs to be run once - hence the before caching
	 */
    @Override
    public RunPhase getRunPhase () {
        return RunPhase.BEFORE_CACHING;
    }

}
