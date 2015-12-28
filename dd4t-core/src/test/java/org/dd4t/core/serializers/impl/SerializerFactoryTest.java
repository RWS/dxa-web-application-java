package org.dd4t.core.serializers.impl;

import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.serializers.Serializer;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.core.util.DateUtils;
import org.dd4t.databind.DataBindFactory;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class SerializerFactoryTest {
    @Before
    public void setUp () throws Exception {
        // Load Spring
        ApplicationContext context = new ClassPathXmlApplicationContext("application-context.xml");

        Serializer serializer = new org.dd4t.core.serializers.impl.json.JSONSerializer();
        SerializerFactory.setSerializer(serializer);
    }

    @Test
    public void testDeserializePage () throws Exception {

        String notFoundPage = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("test.json").toURI()));

        String pageSource = CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(notFoundPage));

        // Deserialize Test Content

        // TODO: move away from the SerializerFactory for Pages and CPs
        //Page page = SerializerFactory.deserialize(pageSource, PageImpl.class);
        Page page = DataBindFactory.buildPage(pageSource, PageImpl.class);

        DateTime revisionDate = DateUtils.convertStringToDate("2015-05-10T00:03:25");
        assertEquals("RevisionDate", revisionDate, page.getRevisionDate());

        DateTime lastpublishDate = DateUtils.convertStringToDate("0001-01-01T00:00:00");
        assertEquals("LastPublishDate", lastpublishDate, page.getLastPublishedDate());

        assertEquals("FileName", "404", page.getFileName());
        assertEquals("FileExtension", "html", page.getFileExtension());
        assertEquals("Title", "404", page.getTitle());

        DateTime pageTemplateRevisionDate = DateUtils.convertStringToDate("2015-05-06T21:45:55.913");
        assertEquals("PageTemplate:RevisionDate", pageTemplateRevisionDate, page.getPageTemplate().getRevisionDate());

        assertEquals("PageId", "tcm:7-112-64", page.getId());

    }
}