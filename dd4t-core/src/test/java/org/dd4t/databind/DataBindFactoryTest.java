package org.dd4t.databind;

import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.PageImpl;
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
    @Before
    public void setUp () throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");
    }

    @Test
    public void testDataBindFactory () throws SerializationException, URISyntaxException, IOException {
        String page = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));
        Page deserializedPage = DataBindFactory.buildPage(CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(page)), PageImpl.class);
        Assert.notNull(deserializedPage, "page cannot be bound");
        Assert.hasLength(deserializedPage.getTitle(), "page has no valid title");
    }
}
