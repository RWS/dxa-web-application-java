package org.dd4t.core.serializers.impl;

import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.serializers.Serializer;
import org.dd4t.core.util.DateUtils;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SerializerFactoryTest {
    @Before
    public void setUp() throws Exception {
        Serializer serializer = new org.dd4t.core.serializers.impl.json.JSONSerializer();
	    SerializerFactory.setSerializer(serializer);
    }

    @Ignore
    @Test
    public void testDeserializePage() throws Exception {

        // TODO: add test data
        String pageSource = "";
        // Deserialize Test Content
        Page page = SerializerFactory.deserialize(pageSource, PageImpl.class);


        DateTime revisionDate = DateUtils.convertStringToDate("2014-06-25T23:40:51.433");
        assertEquals("RevisionDate", revisionDate, page.getRevisionDate());

        DateTime lastpublishDate = DateUtils.convertStringToDate("0001-01-01T00:00:00");
        assertEquals("LastPublishDate", lastpublishDate, page.getLastPublishedDate());

        assertEquals("FileName", "label", page.getFileName());
        assertEquals("FileExtension", "properties", page.getFileExtension());
        assertEquals("Title", "Label", page.getTitle());

        DateTime pageTemplateRevisionDate = DateUtils.convertStringToDate("2014-07-02T19:30:02.24");
        assertEquals("PageTemplate:RevisionDate", pageTemplateRevisionDate, page.getPageTemplate().getRevisionDate());

        assertEquals("PageId", "tcm:13-382-64", page.getId());

    }
}