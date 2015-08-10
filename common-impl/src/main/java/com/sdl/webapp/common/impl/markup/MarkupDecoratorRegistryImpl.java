package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.markup.MarkupDecorator;
import com.sdl.webapp.common.markup.MarkupDecoratorRegistry;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * MarkupDecoratorRegistryImpl
 *
 * @author nic
 */
@Component
public class MarkupDecoratorRegistryImpl implements MarkupDecoratorRegistry {

    private Map<String, List<MarkupDecorator>> markupDecorators = new HashMap<>();


    static class MarkupDecoratorComparator implements Comparator<MarkupDecorator> {

        @Override
        public int compare(MarkupDecorator o1, MarkupDecorator o2) {
            return o1.getPriority() - o2.getPriority();
        }
    }

    @Override
    public void registerDecorator(String decoratorId, MarkupDecorator decorator) {
        List<MarkupDecorator> decoratorList = this.markupDecorators.get(decoratorId);
        if ( decoratorList == null ) {
            decoratorList = new ArrayList<>();
            this.markupDecorators.put(decoratorId, decoratorList);
        }
        decoratorList.add(decorator);
        Collections.sort(decoratorList, new MarkupDecoratorComparator());
    }


    @Override
    public List<MarkupDecorator> getDecorators(String decoratorId) {
        List<MarkupDecorator> decoratorList = this.markupDecorators.get(decoratorId);
        if ( decoratorList == null ) {
            decoratorList = Collections.emptyList();
        }
        return decoratorList;
    }
}
