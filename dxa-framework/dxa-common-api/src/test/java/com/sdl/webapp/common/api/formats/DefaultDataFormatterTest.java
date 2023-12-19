package com.sdl.webapp.common.api.formats;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultDataFormatterTest {

    @Test
    public void shouldGetScoreFromAcceptString() throws Exception {
        //given

        //when
        double q02 = DefaultDataFormatter.getScoreFromAcceptString("audio/*; q=0.2");
        double q00 = DefaultDataFormatter.getScoreFromAcceptString("audio/*; q=0.0");
        double q10 = DefaultDataFormatter.getScoreFromAcceptString("audio/*; q=1.0");
        double q10implicit = DefaultDataFormatter.getScoreFromAcceptString("audio/*");

        //then
        assertEquals(0.2, q02, 0.0);
        assertEquals(0.0, q00, 0.0);
        assertEquals(1.0, q10, 0.0);
        assertEquals(1.0, q10implicit, 0.0);
    }

}