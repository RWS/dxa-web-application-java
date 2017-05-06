package org.dd4t.databind;

import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.ComponentPresentation;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.core.util.CompressionUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.util.Assert;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class DataBindFactoryTest {
	protected ApplicationContext context;
	
    @Before
    public void setUp () throws Exception {
        context = new ClassPathXmlApplicationContext("application-context.xml");
    }

    @Test
    public void testDataBindFactory () throws SerializationException, URISyntaxException, IOException {
        DataBinder databinder = context.getBean(DataBinder.class);
        
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage = databinder.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }

    @Test
    public void testDcpDeserialization() throws URISyntaxException, IOException, SerializationException {
        DataBinder databinder = context.getBean(DataBinder.class);
        
        String dcp = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("testdcpanimal.json").toURI()));
        Assert.notNull(dcp);
        ComponentPresentation componentPresentation = databinder.buildComponentPresentation(dcp, ComponentPresentation.class);
        Assert.notNull(componentPresentation,"DCP cannot be bound");

    }

    @Test
    public void testStaticDataBindFactory () throws SerializationException, URISyntaxException, IOException {


        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage = DataBindFactory.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }
}
