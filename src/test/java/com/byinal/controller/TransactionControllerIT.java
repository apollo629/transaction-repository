package com.byinal.controller;

import com.byinal.model.request.TransactionRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TransactionControllerIT {

    private final static String ENDPOINT = "/transactions";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int serverPort;

    @Test
    public void should_return_http_201_for_valid_request() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("0.0");
        transactionRequest.setTimestamp(Instant.now().toString());

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_204_for_old_tx_request() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("0.0");
        String sixtySecAndOneMillisecAgo = Instant.now()
                .minusMillis(TimeUnit.SECONDS.toMillis(60))
                .minusMillis(TimeUnit.MILLISECONDS.toMillis(1))
                .toString();
        transactionRequest.setTimestamp(sixtySecAndOneMillisecAgo);

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_400_for_invalid_json_request() {
        //given
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> httpEntity = new HttpEntity<>("invalidJsonInput", headers);

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);

        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(),
                HttpMethod.POST,
                httpEntity,
                Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_422_for_request_without_amount() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_422_for_request_without_timestamp() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("19.03");

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_422_for_request_with_unparsable_amount() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("One hunderd");
        transactionRequest.setTimestamp(Instant.now().toString());

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_422_for_request_with_unparsable_timestamp() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("19.03");
        transactionRequest.setTimestamp("19.03.1903 19:03");

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_422_for_request_with_future_timestamp() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("19.03");
        String oneMinLater = Instant.now()
                .plusMillis(TimeUnit.MINUTES.toMillis(1))
                .toString();
        transactionRequest.setTimestamp(oneMinLater);

        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNPROCESSABLE_ENTITY);
        assertThat(response.getBody()).isNull();
    }

    @Test
    public void should_return_http_204_for_delete_request() {
        //when
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + ENDPOINT);
        ResponseEntity<Void> response = testRestTemplate.exchange(builder.build().encode().toUri(), HttpMethod.DELETE, null, Void.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(response.getBody()).isNull();
    }
}