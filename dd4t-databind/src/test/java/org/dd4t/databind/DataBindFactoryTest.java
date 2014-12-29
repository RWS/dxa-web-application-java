package org.dd4t.databind;

import org.dd4t.contentmodel.Page;
import org.dd4t.contentmodel.impl.PageImpl;
import org.dd4t.core.exceptions.SerializationException;
import org.dd4t.databind.builder.json.JsonDataBinder;
import org.junit.Before;
import org.junit.Test;
import org.springframework.util.Assert;

/**
 * Created by Quirijn on 26-12-2014.
 */
public class DataBindFactoryTest {

    @Before
    public void setUp() throws Exception {
        DataBindFactory.getInstance().setDataBinder(JsonDataBinder.getInstance());
    }

    @Test
    public void testDataBindFactory() throws SerializationException {
        // TODO: add JSON source code for the page
        Page page = DataBindFactory.buildPage("", PageImpl.class);
        Assert.notNull(page, "page cannot be bound");
        Assert.hasLength(page.getTitle(), "page has no valid title");
    }
}
