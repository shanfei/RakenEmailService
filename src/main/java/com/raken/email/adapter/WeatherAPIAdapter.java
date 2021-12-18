package com.raken.email.adapter;

import com.google.gson.Gson;
import com.raken.email.adapter.model.WeatherDataModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@Component
public class WeatherAPIAdapter {

       private Logger logger = LoggerFactory.getLogger(this.getClass());

       @Autowired
       public WeatherAPIAdapter(RestTemplate restTemplate) {
              this.restTemplate = restTemplate;
       }

       private RestTemplate restTemplate;

       @Value("${app.api.weatherService}")
       private String api;

       @Value("${app.api.weatherService.key}")
       private String key;

       @Value("${app.api.weatherService.city}")
       private String city;

       @Async
       public CompletableFuture<Optional<String>> process() {

              String requestURI = this.api + "?key=" + key + "&q=" + city;
              Optional<String> ret = Optional.empty();
              try {
                     ResponseEntity<WeatherDataModel> result  = restTemplate.getForEntity(requestURI, WeatherDataModel.class);
                     logger.debug("Result From getWeatherAPI: {} " , result);
                     ret = Optional.ofNullable(result.getBody().getCurrent().getCondition().getText());
              } catch (Exception ex) {
                     logger.error(ex.getMessage(), ex);
              }

              return CompletableFuture.completedFuture(ret);
       }

}
