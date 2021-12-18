package com.raken.email.service;


import com.raken.email.adapter.EmailDeliveryAPIAdapter;
import com.raken.email.adapter.QuoteOfDayAPIAdapter;
import com.raken.email.adapter.WeatherAPIAdapter;
import com.raken.email.model.Email;
import com.raken.email.model.EmailDeliveryStatus;
import com.raken.email.repository.EmailRepository;
import com.raken.email.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

public class EmailServiceTest {

     @InjectMocks
     @Spy
     EmailService emailService;

     @Mock
     private EmailRepository emailRepository;

     @Mock
     private EmailDeliveryAPIAdapter emailDeliveryAPIAdapter;

     @Mock
     private QuoteOfDayAPIAdapter quoteOfDayAPIAdapter;

     @Mock
     private WeatherAPIAdapter weatherAPIAdapter;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSendWithEnrich() {

        Email savedEmail = new Email();
        savedEmail.setId(1l);
        when(emailRepository.save(any())).thenReturn(savedEmail);
        emailService.needFilterAndLog = true;
        doNothing().when(emailService).enrichAndSent(any());

        Long id = emailService.send(new Email(), true);
        assertEquals(Long.valueOf(1), id);
    }

    @Test
    public void testSendWithoutEnrich() {

        Email savedEmail = new Email();
        savedEmail.setId(1l);
        when(emailRepository.save(any())).thenReturn(savedEmail);
        emailService.needFilterAndLog = false;
        doNothing().when(emailService).sendAndUpdate(any());

        Long id = emailService.send(new Email(), false);
        assertEquals(Long.valueOf(1), id);
    }

    @Test
    public void testSendAndUpdateSuccess() {

        when(emailDeliveryAPIAdapter.process(any())).thenReturn(true);
        when(this.emailRepository.save(any())).thenReturn(new Email());

        Email email = new Email();
        email.setStatus(EmailDeliveryStatus.CREATE);

        emailService.sendAndUpdate(email);

        assertEquals(EmailDeliveryStatus.SUCCESS, email.getStatus());

    }

    @Test
    public void testSendAndUpdateFail() {

        when(emailDeliveryAPIAdapter.process(any())).thenReturn(false);
        when(this.emailRepository.save(any())).thenReturn(new Email());

        Email email = new Email();
        email.setStatus(EmailDeliveryStatus.CREATE);

        emailService.sendAndUpdate(email);

        assertEquals(EmailDeliveryStatus.FAIL, email.getStatus());

    }

    @Test
    public void testFilterOutAndLog() {
         Email email = new Email();
         email.setTo("test@RakenApp.com;test@gmail.com");
         emailService.filterOutAndLog(email);
         assertEquals("test@RakenApp.com", email.getTo());
    }

    @Test
    public void testEnrichEmailContent() {

        CompletableFuture<Optional<String>> quoteOfDay = CompletableFuture.completedFuture(Optional.ofNullable("qodTest"));
        when(this.quoteOfDayAPIAdapter.process()).thenReturn(quoteOfDay);

        CompletableFuture<Optional<String>> weather = CompletableFuture.completedFuture(Optional.ofNullable("weatherTest"));
        when(this.weatherAPIAdapter.process()).thenReturn(weather);

        String emailEnriched = emailService.enrichEmailContent("test");

        assertTrue(emailEnriched.contains("weatherTest"));
        assertTrue(emailEnriched.contains("qodTest"));


    }
}
