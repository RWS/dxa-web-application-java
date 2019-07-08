package com.sdl.dxa.tridion.annotations;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public interface AnnotationFetcherForValue {
    String fetchAllValues(InitializingBean bean) throws Exception;
}
