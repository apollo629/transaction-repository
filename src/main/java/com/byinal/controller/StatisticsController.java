package com.byinal.controller;

import com.byinal.model.response.StatisticsResponse;
import com.byinal.service.StatisticsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Statistics endpoint to respond summary requests
 */
@RestController
public class StatisticsController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping(value = "/statistics")
    @ResponseStatus(HttpStatus.OK)
    public StatisticsResponse getStats() {
        ZonedDateTime startTime = ZonedDateTime.now();
        logger.info("Get transaction stats started");
        StatisticsResponse statisticsResponse = statisticsService.retrieveStatsResponse();
        logger.info("Get transaction stats ended. Duration: {} in millis. Response: {}",
                ChronoUnit.MILLIS.between(startTime, ZonedDateTime.now()), statisticsResponse);
        return statisticsResponse;
    }
}