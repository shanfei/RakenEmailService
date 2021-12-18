package com.raken.email.adapter;

import com.google.gson.Gson;
import com.raken.email.adapter.model.EmailAddress;
import com.raken.email.adapter.model.EmailContent;
import com.raken.email.adapter.model.EmailDeliveryDataModel;
import com.raken.email.adapter.model.Personalization;
import com.raken.email.model.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class EmailDeliveryAPIAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public EmailDeliveryAPIAdapter(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    private RestTemplate restTemplate;

    @Value("${app.api.emailDeliveryService}")
    protected String api;

    @Value("${app.api.emailDeliveryService.key}")
    protected String key;

    public Boolean process(Email email) {

        String requestURI = this.api;
        boolean ret = false;

        HttpHeaders headers = this.getHeader();
        EmailDeliveryDataModel emailDeliveryDataModel = this.mapEmailToRequest(email);

        HttpEntity<EmailDeliveryDataModel> request =
                new HttpEntity<>(emailDeliveryDataModel, headers);

        try {
            ResponseEntity<String> result  = restTemplate.postForEntity(requestURI, request, String.class);

            if (result != null) {
                logger.debug("API SendEmail response code: " + result.getStatusCode());
            }

            if (result != null && result.getStatusCode() == HttpStatus.ACCEPTED) {
                ret = true;
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
        }

        return ret;
    }

    protected EmailDeliveryDataModel mapEmailToRequest(Email email) {
        EmailDeliveryDataModel emailDeliveryDataModel = new EmailDeliveryDataModel();

        emailDeliveryDataModel.setPersonalizations(new LinkedList<>());
        Personalization personalization = new Personalization();
        if (email.getTo() != null) {
            personalization.setTo(Arrays.stream(email.getTo().split(";")).map(mailTo -> {
                EmailAddress emailAddress = new EmailAddress();
                emailAddress.setEmail(mailTo);
                return emailAddress;
            }).collect(Collectors.toList()));
        }
        emailDeliveryDataModel.getPersonalizations().add(personalization);

        EmailAddress from = new EmailAddress();
        from.setEmail(email.getFrom());
        emailDeliveryDataModel.setFrom(from);

        emailDeliveryDataModel.setSubject(email.getSubject());

        emailDeliveryDataModel.setContent(new LinkedList<>());
        EmailContent emailContent = new EmailContent();
        emailContent.setValue(email.getBody());
        emailDeliveryDataModel.getContent().add(emailContent);

        return emailDeliveryDataModel;
    }

    protected HttpHeaders getHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(this.key);
        return headers;
    }

}
