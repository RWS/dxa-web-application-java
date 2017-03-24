package com.sdl.dxa.tridion;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Configuration
@EnableCaching
public class R2SpringConfiguration {

    @Autowired
    @Qualifier("dxaR2ObjectMapper")
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate modelServiceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(0, dxaR2MappingJackson2HttpMessageConverter());
        return restTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter dxaR2MappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
}
