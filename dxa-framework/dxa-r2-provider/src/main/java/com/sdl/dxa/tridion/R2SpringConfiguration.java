package com.sdl.dxa.tridion;

import com.sdl.dxa.R2;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@R2
@Slf4j
@Configuration
public class R2SpringConfiguration {

    @Autowired
    private MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Bean
    public RestTemplate modelServiceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, mappingJackson2HttpMessageConverter);
        return restTemplate;
    }
}
