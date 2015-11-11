package org.example.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = TestConfig.class)
public class MainServiceTest {

    @Autowired
    private MainService mainService;

    @Test
    public void shouldHello() throws Exception {
        //given
        final String expected = "Hello, $artifactId!";

        //when
        final String result = mainService.hello();

        //then
        assertEquals(expected, result);
    }
}