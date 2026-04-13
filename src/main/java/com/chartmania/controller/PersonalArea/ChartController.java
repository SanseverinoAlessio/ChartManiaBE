package com.chartmania.controller.PersonalArea;

import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;

import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.chart.ChartsResponseDTO;
import com.chartmania.dto.chart.CreateChartRequest;
import com.chartmania.dto.chart.GetChartRequest.ChartDTO;
import com.chartmania.dto.muidatagrid.MuiDataGridRequestDTO;
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

    public ChartController(ChartService chartService, MuiDataGridService muiDataGridService,
            ChartRepository chartRepository) {
        this.chartService = chartService;
        this.muiDataGridService = muiDataGridService;
        this.chartRepository = chartRepository;
    }

    @GetMapping("charts")
    public ResponseEntity<GenericResponseDTO> getCharts(@Valid MuiDataGridRequestDTO request) {
        Page<Chart> pageChart = this.muiDataGridService.getData(this.chartRepository, request);
        Page<ChartsResponseDTO> pageDto = pageChart.map(chart -> ChartsResponseDTO.fromEntity(chart));

        try {
            return ResponseEntity.status(200).body(new GenericResponseDTO<>(true, "", pageDto));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(new GenericResponseDTO<>(false, e.toString()));
        }
    }

    @GetMapping("charts/{id}")
    public ResponseEntity<GenericResponseDTO> getChart(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id) throws ResponseStatusException {
        Long userId = jwt.getClaim("userId");
        GenericResponseDTO<ChartDTO> response = chartService.getChart(userId, id);
        
        return response.isSuccess() ? ResponseEntity.status(200).body(response)
                : ResponseEntity.status(500).body(response);
    }

    @GetMapping("/charts/{id}/image")
    public ResponseEntity<Resource> getChartImage(@AuthenticationPrincipal Jwt jwt, @PathVariable Long id)
            throws NoSuchFileException, MalformedURLException {
        Long userId = jwt.getClaim("userId");

        Resource response = chartService.getChartImage(userId, id);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .cacheControl(CacheControl.noStore())
                .body(response);
    }

    @PostMapping("/charts/create")
    public ResponseEntity<GenericResponseDTO> create(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateChartRequest requestData) {
        Long userId = jwt.getClaim("userId");
        GenericResponseDTO response = chartService.createChart(userId, requestData);

        return response.isSuccess() ? ResponseEntity.status(200).body(response)
                : ResponseEntity.status(500).body(response);
    }

    @PutMapping("/charts/{id}")
    public ResponseEntity<GenericResponseDTO> update(@AuthenticationPrincipal Jwt jwt,
            @Valid @RequestBody CreateChartRequest requestData, @PathVariable Long id) {
        Long userId = jwt.getClaim("userId");
        GenericResponseDTO response = chartService.updateChart(userId, id, requestData);

        return response.isSuccess() ? ResponseEntity.status(200).body(response)
                : ResponseEntity.status(500).body(response);
    }

    @DeleteMapping("/charts/{id}")
    public ResponseEntity<GenericResponseDTO> delete(@AuthenticationPrincipal Jwt jwt,
            @PathVariable("id") Long chartId) {
        Long userId = jwt.getClaim("userId");

        GenericResponseDTO response = chartService.deleteChart(userId, chartId);

        return response.isSuccess() ? ResponseEntity.status(200).body(response)
                : ResponseEntity.status(500).body(response);
    }
}
