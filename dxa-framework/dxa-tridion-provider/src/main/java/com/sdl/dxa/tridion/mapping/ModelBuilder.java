package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.impl.DefaultModelBuilder;
import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipelineImpl;
import org.springframework.core.Ordered;

/**
 * Builds a strongly typed DXA Model based on a given DXA R2 Data Model.
 * <p>Implementations of known sub-interfaces ({@link EntityModelBuilder} and {@link PageModelBuilder}) are added to a {@link ModelBuilderPipelineImpl}
 * accordingly to their order defined by {@link Ordered#getOrder()}.
 * {@linkplain DefaultModelBuilder Default basic implementation} gets the highest precedence which shouldn't be overridden,
 * other DXA default implementations get the order number between {@linkplain Ordered#HIGHEST_PRECEDENCE highest precedence}
 * and {@code 0} with approximate step {@code 1000}.</p>
 * <p>Custom implementations are expected to have <code>0 &lt; order number &gt; {@link Ordered#LOWEST_PRECEDENCE}</code>
 * unless they are required to be injected in the middle of default implementations.
 * Default ones are still expected to be the first.</p>
 *
 * @dxa.publicApi
 * @see EntityModelData
 * @see PageModelData
 * @see EntityModelBuilder
 * @see PageModelBuilder
 * @see ModelBuilderPipelineImpl
 */
public interface ModelBuilder extends Ordered {

}
