package com.raken.email.controller;


import com.raken.email.controller.EmailController;
import com.raken.email.model.Email;
import com.raken.email.service.EmailService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


public class EmailControllerTest {

    @InjectMocks
    EmailController emailController;

    @Before
    public void init() {
        MockitoAnnotations.openMocks(this);
    }

    @Mock
    private EmailService emailService;

    @Test
    public void testWhenCreateEmailShouldReturnCreatedStatus() throws Exception {

           when(emailService.send(any(), any())).thenReturn(1l);

           Email email = new Email();
           email.setBody("test");
           email.setTo("aa@gmail.com");
           email.setFrom("test@gmail.com");
           email.setSubject("test");

           ResponseEntity entity = emailController.sendEmail(false, email);

           assertEquals(HttpStatus.CREATED, entity.getStatusCode());
           assertEquals("/api/v1/email/1", entity.getHeaders().getLocation().toString());

    }

}
