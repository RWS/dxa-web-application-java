package org.dd4t.core.serializers.impl;

import org.apache.commons.io.FileUtils;
import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.databind.DataBinder;
import org.dd4t.core.util.CompressionUtils;
import org.dd4t.core.util.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.File;

import static org.junit.Assert.assertEquals;

public class SerializerFactoryTest {

	protected ApplicationContext context;

    @Before
    public void setUp () throws Exception {
        // Load Spring
        context = new ClassPathXmlApplicationContext("application-context.xml");
    }

    @Test
    public void testDeserializePage () throws Exception {

        String testPage = FileUtils.readFileToString(new File(ClassLoader.getSystemResource("fulltestencoded.json").toURI()));

        String pageSource = CompressionUtils.decompressGZip(CompressionUtils.decodeBase64(testPage));

        // TODO: move away from the SerializerFactory for Pages and CPs
        //Page page = SerializerFactory.deserialize(pageSource, PageImpl.class);
        // loading item by interface; perhaps by name is better depending on the context
        DataBinder databinder = context.getBean(DataBinder.class);
        Page page = databinder.buildPage(pageSource, PageImpl.class);

        DateTime revisionDate = DateUtils.convertStringToDate("2016-03-05T11:40:25");
        assertEquals("RevisionDate", revisionDate, page.getRevisionDate());

        DateTime lastpublishDate = DateUtils.convertStringToDate("0001-01-01T00:00:00");
        assertEquals("LastPublishDate", lastpublishDate, page.getLastPublishedDate());

        assertEquals("FileName", "index", page.getFileName());
        assertEquals("FileExtension", "html", page.getFileExtension());
        assertEquals("Title", "Homepage", page.getTitle());

        DateTime pageTemplateRevisionDate = DateUtils.convertStringToDate("2016-08-10T14:42:30.170");
        assertEquals("PageTemplate:RevisionDate", pageTemplateRevisionDate, page.getPageTemplate().getRevisionDate());

        assertEquals("PageId", "tcm:7-108-64-v0", page.getId());

    }
}