package com.raken.email.adapter;

import com.raken.email.model.Email;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class EmailDeliveryAPIAdapterTest {

       @InjectMocks
       EmailDeliveryAPIAdapter emailDeliveryAPIAdapter;

       @Mock
       RestTemplate restTemplate;

        @Before
        public void init() {
            MockitoAnnotations.openMocks(this);
        }

       @Test
       public void testProcessSuccess() {

           emailDeliveryAPIAdapter.api = "testAPI";
           ResponseEntity mockResponseEntity = mock(ResponseEntity.class);
           when(restTemplate.postForEntity(eq("testAPI"), any(HttpEntity.class), eq(String.class))).thenReturn(mockResponseEntity);
           when(mockResponseEntity.getStatusCode()).thenReturn(HttpStatus.ACCEPTED);

           Email email = new Email();

           Boolean result = emailDeliveryAPIAdapter.process(email);

           assertEquals(true, result);
       }

    @Test
    public void testProcessFail() {

        when(restTemplate.postForEntity(any(), any(), any())).thenThrow(NullPointerException.class);

        Email email = new Email();

        Boolean result = emailDeliveryAPIAdapter.process(email);

        assertEquals(false, result);
    }


}
