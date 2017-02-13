package com.sdl.dxa.tridion.mapping;

import com.sdl.dxa.api.datamodel.model.EntityModelData;
import com.sdl.dxa.api.datamodel.model.PageModelData;
import com.sdl.dxa.tridion.mapping.impl.DefaultModelBuilder;
import com.sdl.dxa.tridion.mapping.impl.ModelBuilderPipeline;
import org.springframework.core.Ordered;

/**
 * Builds a strongly typed DXA Model based on a given DXA R2 Data Model.
 * <p>Implementations of known sub-interfaces ({@link EntityModelBuilder} and {@link PageModelBuilder}) are added to a {@link ModelBuilderPipeline}
 * accordingly to their order defined by {@link Ordered#getOrder()}.
 * {@linkplain DefaultModelBuilder Default basic implementation} gets the highest precedence which shouldn't be overridden,
 * other DXA default implementations get the order number between {@linkplain Ordered#HIGHEST_PRECEDENCE highest precedence}
 * and {@code 0} with approximate step {@code 1000}.</p>
 * <p>Custom implementations are expected to have <code>0 &lt; order number &gt; {@link Ordered#LOWEST_PRECEDENCE}</code>
 * unless they are required to be injected in the middle of default implementations.
 * Default ones are still expected to be the first.</p>
 *
 * @see EntityModelData
 * @see PageModelData
 * @see EntityModelBuilder
 * @see PageModelBuilder
 * @see ModelBuilderPipeline
 */
public interface ModelBuilder extends Ordered {

    /**
     * Please refer to {@link ModelBuilder} documentation to choose the returned value for this method.
     * The original documentation for {@link Ordered#getOrder()} should also be useful.
     *
     * @see ModelBuilder
     * @see Ordered#getOrder()
     */
    @Override
    int getOrder();
}
