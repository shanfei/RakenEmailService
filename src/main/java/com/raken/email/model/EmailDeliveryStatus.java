package com.raken.email.model;

public enum EmailDeliveryStatus {

      CREATE("create"), FAIL("fail"), SUCCESS("success");
      String status;

      EmailDeliveryStatus(String status) {
            this.status = status;
      }
}
