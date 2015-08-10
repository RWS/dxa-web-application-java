package com.sdl.webapp.common.impl.markup;

import com.sdl.webapp.common.api.model.region.RegionImpl;
import com.sdl.webapp.common.markup.Markup;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * Unit tests for {@code Markup}.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = MarkupTestConfig.class)
public class MarkupTest {

    @Autowired
    private Markup markup;

    @Test
    public void testUrl() {
        assertThat(markup.url("/example"), is("/test/example"));
    }

    @Test
    public void testVersionedContent() {
        assertThat(markup.versionedContent("/example"), is("/test/xyz/system/v0.5/example"));
    }

    @Test
    public void testRegion() {
        final RegionImpl region = new RegionImpl();
        region.setName("TestRegion");
        assertThat(markup.region(region), is("typeof=\"Region\" resource=\"TestRegion\""));
    }

    @Test
    public void testEntity() {
        assertThat(markup.entity(new MarkupTestConfig.TestEntity()),
                is("prefix=\"s: http://schema.org/\" typeof=\"s:SchemaEnt\""));
    }

    @Test
    public void testPublicProperty() {
        assertThat(markup.property(new MarkupTestConfig.TestEntity(), "testField"), is("property=\"s:TheField\""));
    }

    @Test
    public void testPrivateProperty() {
        // Field which has an empty prefix; the vocabulary with the empty prefix is implicitly private,
        // so there should be no 'property' attribute for this field
        assertThat(markup.property(new MarkupTestConfig.TestEntity(), "hiddenField"), is(""));
    }

    @Test
    public void testResource() {
        assertThat(markup.resource("core.todayText"), is("TODAY"));
    }

    @Test
    public void testFormatDate() {
        assertThat(markup.formatDate(new DateTime(2014, 12, 11, 15, 39, 8, 834)), is("Thursday, December 11, 2014"));
    }

    @Test
    public void testFormatDateDiffToday() {
        assertThat(markup.formatDateDiff(DateTime.now()), is("TODAY"));
    }

    @Test
    public void testFormatDateDiffYesterday() {
        assertThat(markup.formatDateDiff(DateTime.now().minusDays(1)), is("YESTERDAY"));
    }

    @Test
    public void testFormatDateDiffXDaysAgo() {
        for (int i = 2; i <= 7; i++) {
            assertThat(markup.formatDateDiff(DateTime.now().minusDays(i)), is(i + " DAYS AGO"));
        }
    }

    @Test
    public void testFormatDateDiffLongerAgo() {
        for (int i = 8; i < 400; i++) {
            final DateTime dateTime = DateTime.now().minusDays(i);
            final String expected = DateTimeFormat.forPattern("d MMM yyyy").withLocale(Locale.US).print(dateTime);
            assertThat(markup.formatDateDiff(dateTime), is(expected));
        }
    }

    @Test
    public void testFormatMessage() {
        assertThat(markup.formatMessage("Hello {0}, the weather is {1} today", "World", "cold"),
                is("Hello World, the weather is cold today"));
    }

    @Test
    public void testReplaceLineEndsWithHtmlBreaks() {
        assertThat(markup.replaceLineEndsWithHtmlBreaks("Hello. Sunny World."), is("Hello<br/>Sunny World."));
    }
}
