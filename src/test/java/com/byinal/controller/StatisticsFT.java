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
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class StatisticsFT {

    private final static String TX_ENDPOINT = "/transactions";
    private final static String STATS_ENDPOINT = "/statistics";

    @Autowired
    private TestRestTemplate testRestTemplate;
    private UriComponentsBuilder txUriBuilder;
    private UriComponentsBuilder statsUriBuilder;

    @LocalServerPort
    private int serverPort;

    @Before
    public void setUp() {
        txUriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + TX_ENDPOINT);
        statsUriBuilder = UriComponentsBuilder.fromHttpUrl("http://localhost:" + serverPort + STATS_ENDPOINT);
    }

    @Test
    public void should_calculate_stats_successfully_for_valid_requests() {
        //when
        IntStream.rangeClosed(1, 1903)
                .forEach(index -> {
                    TransactionRequest request = new TransactionRequest();
                    request.setAmount(String.valueOf(index));
                    request.setTimestamp(Instant.now().minusMillis(index).toString());
                    testRestTemplate.exchange(txUriBuilder.build().encode().toUri(), HttpMethod.POST, new HttpEntity<>(request), Void.class);
                });

        ResponseEntity<StatisticsResponse> response = testRestTemplate.exchange(statsUriBuilder.build().encode().toUri(), HttpMethod.GET, null, StatisticsResponse.class);

        //then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isInstanceOf(StatisticsResponse.class);
        assertThat(response.getBody())
                .extracting("count", "min", "max", "sum", "avg")
                .containsExactly(1903L, "1.00", "1903.00", "1811656.00", "952.00");

    }
}
