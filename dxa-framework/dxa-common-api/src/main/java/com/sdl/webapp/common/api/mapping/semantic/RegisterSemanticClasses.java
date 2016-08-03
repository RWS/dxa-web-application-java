package com.sdl.webapp.common.api.mapping.semantic;

import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * Register Semantic Classes Bean.
 * Spring bean to register semantic classes to DXA.
 * Example of usage:
 * <pre>
 *  {@code
 *
 *      <!-- Register one namespace -->
 *      <bean class="com.sdl.webapp.common.api.mapping.SemanticClasses">
 *          <property name="namespace" value="com.sdl.dxa.modules.example.model"/>
 *      </bean>
 *
 *      <!-- Register several namespaces -->
 *      <bean class="com.sdl.webapp.common.api.mapping.SemanticClasses">
 *          <property name="namespaces">
 *              <list>
 *                  <value>com.sdl.dxa.modules.example.model</value>
 *                  <value>com.sdl.dxa.modules.example.model2</value>
 *                  <value>com.sdl.dxa.modules.example.model3</value>
 *              </list>
 *          </property>
 *      </bean>
 *
 * }
 *  </pre>
 *
 * @author nic
 * @deprecated since 1.6
 */
@Deprecated
//todo dxa2 remove, not used
public class RegisterSemanticClasses {

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    @Setter
    private String namespace;

    @Setter
    private List<String> namespaces;

        /**
     * <p>initialize.</p>
     */
    @PostConstruct
    public void initialize() {
        if (namespace != null) {
            semanticMappingRegistry.registerEntities(namespace);
        } else if (namespaces != null) {
            for (String namespace : namespaces) {
                semanticMappingRegistry.registerEntities(namespace);
            }
        }
    }
}
