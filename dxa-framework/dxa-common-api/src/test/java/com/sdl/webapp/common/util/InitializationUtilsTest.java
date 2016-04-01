package com.sdl.webapp.common.util;

import org.junit.Test;
import org.springframework.core.io.Resource;

import java.util.Collection;
import java.util.Properties;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasProperty;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

public class InitializationUtilsTest {

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGetAllResources() throws Exception {
        //when
        Collection<Resource> resources = InitializationUtils.getAllResources();

        //then
        assertEquals(5, resources.size());
        assertThat(resources, contains(
                // contains() also check the order of items
                hasProperty("path", containsString("dxa.defaults.properties")),
                hasProperty("path", containsString("dxa.modules.cid.properties")),
                hasProperty("path", containsString("dxa.modules.xo.properties")),
                hasProperty("path", containsString("dxa.properties")),
                hasProperty("path", containsString("dxa.addons.staging.properties"))));
    }

    @Test
    public void shouldLoadResourcesOnlyOnce() {
        //when
        Collection<Resource> resources = InitializationUtils.getAllResources();
        Collection<Resource> resources2 = InitializationUtils.getAllResources();

        //then
        assertSame(resources, resources2);
    }

    @Test
    public void shouldLoadPropertiesOnlyOnce() {
        //when
        Properties properties = InitializationUtils.loadDxaProperties();
        Properties properties2 = InitializationUtils.loadDxaProperties();

        //then
        assertSame(properties, properties2);
    }

}