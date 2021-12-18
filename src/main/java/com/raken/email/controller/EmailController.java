package com.raken.email.controller;

import com.raken.email.model.Email;
import com.raken.email.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;

@RestController
public class EmailController {

       private EmailService emailService;

       @Autowired
       public EmailController(EmailService emailService) {
            this.emailService = emailService;
       }

       @PostMapping(path = "/api/v1/email/", consumes = MediaType.APPLICATION_JSON_VALUE)
       public ResponseEntity sendEmail(@RequestParam(required = false) Boolean enrich,
                                   @RequestBody @Valid Email email) {

               Long emailId = this.emailService.send(email, enrich);

               URI location = URI.create("/api/v1/email/" + emailId);

               return ResponseEntity.created(location).build();
       }

    @GetMapping("/api/v1/email/{id}")
    public ResponseEntity getEmail(@PathVariable("id") Long emailId) {

        Email email = this.emailService.getEmail(emailId);

        if (email != null) {
            return ResponseEntity.ok(email);
        } else {
            return ResponseEntity.notFound().build();
        }
    }





}
