package $package;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class)
@ActiveProfiles("test")
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

    @Configuration
    @Profile("test")
    public static class TestConfig {
        @Bean
        public MainService mainService() {
            return new MainService();
        }
    }
}