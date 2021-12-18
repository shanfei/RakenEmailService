package com.raken.email.adapter;

import com.google.gson.Gson;
import com.raken.email.adapter.model.QuotesOfDayDataModel;
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
public class QuoteOfDayAPIAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public QuoteOfDayAPIAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RestTemplate restTemplate;


    @Value("${app.api.quoteOfDayService}")
    private String api;

    @Async
    public CompletableFuture<Optional<String>> process() {

        String requestURI = this.api;
        Optional<String> ret = Optional.empty();
        try {
            ResponseEntity<QuotesOfDayDataModel> result  = restTemplate.getForEntity(requestURI, QuotesOfDayDataModel.class);
            logger.debug("Result From getQuotesOfDay API: " +  result);
            ret = Optional.ofNullable(result.getBody().getContents().getQuotes().get(0).getQuote());
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return CompletableFuture.completedFuture(ret);
    }

}
