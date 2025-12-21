package com.chartmania.service;

import org.springframework.stereotype.Service;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.chart.CreateChartRequest;
import com.chartmania.model.Chart;
import com.chartmania.repository.ChartRepository;

@Service
public class ChartService {
    private final ChartRepository chartRepository;
    public ChartService(ChartRepository chartRepository) {
        this.chartRepository = chartRepository;
    }

    public GenericResponseDTO createChart(CreateChartRequest data) {
        Chart chart = new Chart(data.getName(), data.getType(), data.getJson());
        try {
            chartRepository.save(chart);
            return new GenericResponseDTO(true, "Chart created");
        } catch (Exception e) {
            return new GenericResponseDTO(true, "couldn't create chart");
        }
    }

}
