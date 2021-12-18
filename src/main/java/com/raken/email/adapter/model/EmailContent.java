package com.raken.email.adapter.model;

import lombok.Data;

@Data
public class EmailContent {
    private String type = "text/plain";
    private String value;
}
