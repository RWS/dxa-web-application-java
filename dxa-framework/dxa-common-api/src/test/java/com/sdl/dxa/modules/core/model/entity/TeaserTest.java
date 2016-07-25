package com.sdl.dxa.modules.core.model.entity;

import com.sdl.webapp.common.api.model.RichText;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

public class TeaserTest {

    @Test
    public void shouldHaveCustomGetterForTestEnsuringAlwaysNotNullRichText() {
        //given
        Teaser teaser = new Teaser();

        Teaser teaser2 = new Teaser();
        RichText richText = new RichText("hello");
        teaser2.setText(richText);

        //when
        RichText text = teaser.getText();
        RichText text2 = teaser2.getText();

        //then
        assertNotNull(text);
        assertNotNull(text2);
        assertSame(richText, text2);
    }

}