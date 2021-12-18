package com.raken.email.config;

import com.google.gson.Gson;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    RestTemplate getRestTemplate() {
        return new RestTemplate();
    }

    @Bean
    Gson getGson() {
        return new Gson();
    }
}
