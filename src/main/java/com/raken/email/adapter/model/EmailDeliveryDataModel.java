package com.raken.email.adapter.model;

import lombok.Data;

import java.util.List;

@Data
public class EmailDeliveryDataModel {

     private List<Personalization> personalizations;
     private EmailAddress from;
     private String subject;
     private List<EmailContent> content;


}
