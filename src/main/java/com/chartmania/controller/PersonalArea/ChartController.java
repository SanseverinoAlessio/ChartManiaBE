package com.chartmania.controller.PersonalArea;

import org.springframework.boot.security.oauth2.server.resource.autoconfigure.OAuth2ResourceServerProperties.Jwt;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.chartmania.dto.muidatagrid.MuiDataGridRequestDTO;
import com.chartmania.dto.chart.CreateChartRequest;
import com.chartmania.model.Chart;
import com.chartmania.repository.ChartRepository;
import com.chartmania.service.ChartService;
import com.chartmania.service.MuiDataGridService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/personal-area")
public class ChartController {
    private ChartService chartService;
    private MuiDataGridService muiDataGridService;
    private ChartRepository chartRepository;

    public ChartController(ChartService chartService, MuiDataGridService muiDataGridService,ChartRepository chartRepository) {
        this.chartService = chartService;
        this.muiDataGridService = muiDataGridService;
        this.chartRepository = chartRepository;
    }

    @GetMapping("charts")
    public ResponseEntity<Page<Chart>> getCharts(@Valid MuiDataGridRequestDTO request) {
        return ResponseEntity.status(200).body(this.muiDataGridService.getData(this.chartRepository, request));
    }

    @GetMapping("chart/{id}")
    public void getChart() {

    }

    @PostMapping("/chart/create")
    public void create(@AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateChartRequest requestData) {
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
