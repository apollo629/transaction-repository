package com.byinal.controller;

import com.byinal.model.request.TransactionRequest;
import com.byinal.model.response.StatisticsResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsControllerIT {

    private final static String STATS_ENDPOINT = "/statistics";
    private final static String TX_ENDPOINT = "/transactions";

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalServerPort
    private int serverPort;

    private UriComponentsBuilder txUriBuilder;
    private UriComponentsBuilder statsUriBuilder;

    @Before
    public void setUp() {
        txUriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + TX_ENDPOINT);
        statsUriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + STATS_ENDPOINT);
    }

    @Test
    public void should_return_http_200_for_valid_request(){
        //when
        ResponseEntity<StatisticsResponse> response = testRestTemplate.exchange(statsUriBuilder.build().encode().toUri(), HttpMethod.GET, null, StatisticsResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(StatisticsResponse.class);
        testRestTemplate.exchange(txUriBuilder.build().encode().toUri(), HttpMethod.DELETE, null, Void.class);
    }

    @Test
    public void should_return_zero_statistics_before_any_transaction_made() {
        //when
        ResponseEntity<StatisticsResponse> response = testRestTemplate.exchange(statsUriBuilder.build().encode().toUri(), HttpMethod.GET, null, StatisticsResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(StatisticsResponse.class);
        assertThat(response.getBody())
                .extracting("count", "max", "min", "sum", "avg")
                .containsExactly(0L, "0.00", "0.00", "0.00", "0.00");
    }

    @Test
    public void should_return_not_zero_statistics_after_successful_transaction() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("19.03");
        transactionRequest.setTimestamp(Instant.now().toString());

        ResponseEntity<Void> successfulTxResponse = testRestTemplate.exchange(txUriBuilder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);

        //when
        ResponseEntity<StatisticsResponse> response = testRestTemplate.exchange(statsUriBuilder.build().encode().toUri(), HttpMethod.GET, null, StatisticsResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(StatisticsResponse.class);
        assertThat(response.getBody())
                .extracting("count", "max", "min", "sum", "avg")
                .containsExactly(1L, "19.03", "19.03", "19.03", "19.03");
        testRestTemplate.exchange(txUriBuilder.build().encode().toUri(), HttpMethod.DELETE, null, Void.class);
    }

    @Test
    public void should_return_zero_statistics_after_successful_deletion() {
        //given
        TransactionRequest transactionRequest = new TransactionRequest();
        transactionRequest.setAmount("19.03");
        transactionRequest.setTimestamp(Instant.now().toString());

        ResponseEntity<Void> successfulTxResponse = testRestTemplate.exchange(txUriBuilder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(transactionRequest), Void.class);
        ResponseEntity<StatisticsResponse> statsResponseBeforeDeletion = testRestTemplate.exchange(statsUriBuilder.build().encode().toUri(), HttpMethod.GET, null, StatisticsResponse.class);

        // assert before deletion
        assertThat(statsResponseBeforeDeletion.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statsResponseBeforeDeletion.getBody()).isNotNull();
        assertThat(statsResponseBeforeDeletion.getBody()).isInstanceOf(StatisticsResponse.class);
        assertThat(statsResponseBeforeDeletion.getBody())
                .extracting("count", "max", "min", "sum", "avg")
                .containsExactly(1L, "19.03", "19.03", "19.03", "19.03");

        //when
        ResponseEntity<Void> deleteResponse = testRestTemplate.exchange(txUriBuilder.build().encode().toUri(), HttpMethod.DELETE, null, Void.class);
        ResponseEntity<StatisticsResponse> statsResponseAfterDeletion = testRestTemplate.exchange(statsUriBuilder.build().encode().toUri(), HttpMethod.GET, null, StatisticsResponse.class);

        //then assert after deletion
        assertThat(statsResponseAfterDeletion.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(statsResponseAfterDeletion.getBody()).isNotNull();
        assertThat(statsResponseAfterDeletion.getBody()).isInstanceOf(StatisticsResponse.class);
        assertThat(statsResponseAfterDeletion.getBody())
                .extracting("count", "max", "min", "sum", "avg")
                .containsExactly(0L, "0.00", "0.00", "0.00", "0.00");
    }
}