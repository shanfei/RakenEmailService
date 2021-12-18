package com.raken.email.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;


@Entity
@Table(name = "email_tbl")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Email {
       @Id
       @GeneratedValue(strategy=GenerationType.AUTO)
       private Long id;

       @Column(name = "email_from")
       private String from;

       @JsonProperty(required = true)
       private String to;
       private String subject;
       private String body;

       private String cc;
       private String bcc;

       private EmailDeliveryStatus status = EmailDeliveryStatus.CREATE;

}
