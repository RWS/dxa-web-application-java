package org.dd4t.databind;

import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.Component;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.EmbeddedField;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNotNull;

public class DataBindFactoryTest {

    private ApplicationContext applicationContext;

    @Before
    public void setUp () throws Exception {
        applicationContext = new ClassPathXmlApplicationContext("application-context.xml");
    }

    @Test
    public void testDataBindFactory () throws SerializationException, URISyntaxException, IOException {
        DataBinder databinder = applicationContext.getBean(DataBinder.class);

        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage = databinder.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testExtensionDataBindFactory () throws SerializationException, URISyntaxException, IOException {
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("extensiondatapage.json").toURI()));
        DataBinder databinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage = databinder.buildPage(page, PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");


        String serialized = JsonDataBinder.getGenericMapper().writeValueAsString(deserializedPage);

        assertNotNull(serialized);

        String rootFolder = new File(ClassLoader.getSystemResource(".").getPath()).getParentFile().getParentFile().getPath();
        FileUtils.write(new File(rootFolder + "/src/test/resources/testserialized.json"), serialized, "UTF-8");
    }

    @Test
    public void testDcpDeserialization() throws URISyntaxException, IOException, SerializationException {
        DataBinder databinder = applicationContext.getBean(DataBinder.class);

        String dcp = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("testdcpanimal.json").toURI()));
        Assert.notNull(dcp);
        ComponentPresentation componentPresentation = databinder.buildComponentPresentation(dcp, ComponentPresentation.class);
        Assert.notNull(componentPresentation,"DCP cannot be bound");

    }

    @Test
    public void testEmbeddedSerialization() throws URISyntaxException, SerializationException, IOException {
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        DataBinder databinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage = databinder.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);

        EmbeddedField field = (EmbeddedField) deserializedPage.getComponentPresentations().get(0).getComponent().getContent().get("embeddedTest");

        String serialized = JsonDataBinder.getGenericMapper().writeValueAsString(field);

        System.out.println(serialized);

        Component component = deserializedPage.getComponentPresentations().get(0).getComponent();

        String serializedComponent = JsonDataBinder.getGenericMapper().writeValueAsString(component);

        assertNotNull(deserializedPage);

    }

    @Test
    public void testStaticDataBindFactory () throws SerializationException, URISyntaxException, IOException {


        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        DataBinder databinder = applicationContext.getBean(DataBinder.class);
        Page deserializedPage = databinder.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testJsonDataBinder() {
        DataBinder dataBinder = applicationContext.getBean(DataBinder.class);
        assertNotNull(dataBinder);

    }

    @Test
    public void testDcp() throws URISyntaxException, IOException, SerializationException {
        String dcp = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("newitem.json").toURI()));
        DataBinder dataBinder = applicationContext.getBean(DataBinder.class);

        ComponentPresentation componentPresentation = dataBinder.buildComponentPresentation(dcp, ComponentPresentation.class);

        assertNotNull(componentPresentation);
    }
}