package com.sdl.dxa.tridion;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.stream.Collectors;

@Slf4j
@Configuration
public class R2SpringConfiguration {

    @Autowired
    @Qualifier("dxaR2ObjectMapper")
    private ObjectMapper objectMapper;

    @Bean
    public RestTemplate modelServiceRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        // replace the default JSON message converter with R2 on the right place in collection
        restTemplate.setMessageConverters(
                restTemplate.getMessageConverters().stream()
                        .map(converter -> converter instanceof MappingJackson2HttpMessageConverter ?
                                dxaR2MappingJackson2HttpMessageConverter() : converter)
                        .collect(Collectors.toList()));
        return restTemplate;
    }

    @Bean
    public MappingJackson2HttpMessageConverter dxaR2MappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper);
        return jsonConverter;
    }
}
