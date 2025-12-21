package com.chartmania.controller.PersonalArea;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties.Jwt;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.chartmania.dto.chart.CreateChartRequest;
import com.chartmania.service.ChartService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class ChartController {
    private ChartService chartService;

    public ChartController(ChartService chartService) {
        this.chartService = chartService;
    }

    @GetMapping("chart/{id}")
    public void getChart() {
        try{
            
        }
        catch(Exception e){


        }

    }

    @PostMapping("/chart/create")
    public void create(@AuthenticationPrincipal Jwt jwt,@Valid @RequestBody CreateChartRequest requestData) {
        try {
            chartService.createChart(requestData);
        } catch (Exception e) {

        }
    }

    @PutMapping("/chart/{id}")
    public void update() {

    }

    @DeleteMapping("/chart/{id}")
    public void delete() {

    }
}
