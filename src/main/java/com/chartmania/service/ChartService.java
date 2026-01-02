package com.chartmania.service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    public Page<Chart> getCharts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return chartRepository.findAll(pageable);
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

    public GenericResponseDTO deleteChart(Long chartId){
        try{
            chartRepository.deleteById(chartId);
            return new GenericResponseDTO(true, "Chart deleted");
        }
        catch(Exception e){
            return new GenericResponseDTO<>(false, "couldn't delete chart");
        }
    }

    

}
