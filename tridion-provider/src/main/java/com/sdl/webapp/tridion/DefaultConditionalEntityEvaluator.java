package com.sdl.webapp.tridion;

import org.springframework.stereotype.Component;

import com.sdl.webapp.common.api.content.ConditionalEntityEvaluator;
import com.sdl.webapp.common.api.model.EntityModel;

@Component
public class DefaultConditionalEntityEvaluator implements
		ConditionalEntityEvaluator {

	@Override
	public boolean IncludeEntity(EntityModel entity) {
		//TODO : currently, no implementation has been built, we just return true by default
		return true;		
	}
}
