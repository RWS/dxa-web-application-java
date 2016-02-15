package com.sdl.webapp.common.api.mapping.semantic;

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
 */
public class RegisterSemanticClasses {

    @Autowired
    private SemanticMappingRegistry semanticMappingRegistry;

    private String namespace;
    private List<String> namespaces;

    /**
     * <p>Setter for the field <code>namespace</code>.</p>
     *
     * @param namespace a {@link java.lang.String} object.
     */
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    /**
     * <p>Setter for the field <code>namespaces</code>.</p>
     *
     * @param namespaces a {@link java.util.List} object.
     */
    public void setNamespaces(List<String> namespaces) {
        this.namespaces = namespaces;
    }

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
