package com.raken.email.service;

import com.raken.email.adapter.EmailDeliveryAPIAdapter;
import com.raken.email.adapter.QuoteOfDayAPIAdapter;
import com.raken.email.adapter.WeatherAPIAdapter;
import com.raken.email.model.Email;
import com.raken.email.model.EmailDeliveryStatus;
import com.raken.email.repository.EmailRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class EmailService {

       private Logger logger = LoggerFactory.getLogger(this.getClass());

       @Value("${app.needFilterAndLog}")
       public Boolean needFilterAndLog;

       @Autowired
       public EmailService(EmailDeliveryAPIAdapter emailDeliveryAPIAdapter,
                           QuoteOfDayAPIAdapter quoteOfDayAPIAdapter,
                           WeatherAPIAdapter weatherAPIAdapter,
                           EmailRepository emailRepository) {
              this.emailDeliveryAPIAdapter = emailDeliveryAPIAdapter;
              this.quoteOfDayAPIAdapter = quoteOfDayAPIAdapter;
              this.weatherAPIAdapter = weatherAPIAdapter;
              this.emailRepository = emailRepository;
       }

       private EmailRepository emailRepository;

       private EmailDeliveryAPIAdapter emailDeliveryAPIAdapter;

       private QuoteOfDayAPIAdapter quoteOfDayAPIAdapter;

       private WeatherAPIAdapter weatherAPIAdapter;

       public Long send(Email email, Boolean enrich) {

              Email savedEmail =  emailRepository.save(email);

              if (this.needFilterAndLog) {
                   this.filterOutAndLog(savedEmail);
              }

              if (enrich != null && enrich) {
                  enrichAndSent(savedEmail);
              } else {
                  sendAndUpdate(savedEmail);
              }

              return savedEmail.getId();
       }

       public Email getEmail(Long emailId) {
              return this.emailRepository.findById(emailId).orElse(null);
       }

       @Async
       protected void sendAndUpdate(Email email) {
              Boolean sendMailResult = emailDeliveryAPIAdapter.process(email);

              if (sendMailResult != null && sendMailResult) {
                     email.setStatus(EmailDeliveryStatus.SUCCESS);
              } else {
                     email.setStatus(EmailDeliveryStatus.FAIL);
              }

              this.emailRepository.save(email);
       }

       @Async
       protected void enrichAndSent(Email email) {
              String enrichedEmailBody = this.enrichEmailContent(email.getBody());
              email.setBody(enrichedEmailBody);
              sendAndUpdate(email);
       }

       @Async
       protected void filterOutAndLog(Email email) {

              if (email.getTo() == null) return;

              List<String> rakenAppEmailsOfTo = Arrays.stream(email.getTo().split(";"))
                       .filter( e -> e.endsWith("RakenApp.com"))
                      .collect(Collectors.toList());
              List<String> notRakenAppEMailsOfTo = Arrays.stream(email.getTo().split(";"))
                      .filter( e -> !e.endsWith("RakenApp.com"))
                      .collect(Collectors.toList());

              email.setTo(String.join(";", rakenAppEmailsOfTo));

              for (String notRakenAppEmail : notRakenAppEMailsOfTo) {
                     logger.info(notRakenAppEmail);
              }
       }

       protected String enrichEmailContent(String emailBody) {

              StringBuilder sb = new StringBuilder(emailBody);

              CompletableFuture<Optional<String>> quoteOfDay = this.quoteOfDayAPIAdapter.process();
              CompletableFuture<Optional<String>> weather = this.weatherAPIAdapter.process();
              CompletableFuture.allOf(quoteOfDay,weather).join();

              try {
                     sb.append(" Quote Of Day: ").append(quoteOfDay.get().orElse(""));
                     sb.append(" Today's Carlsbad Weather : ").append(weather.get().orElse(""));
              } catch (InterruptedException | ExecutionException ex) {
                     logger.error(ex.getMessage(), ex);
              }

              return sb.toString();

       }


}
