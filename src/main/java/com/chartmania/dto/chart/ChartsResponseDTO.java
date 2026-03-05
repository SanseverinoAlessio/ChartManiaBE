package com.chartmania.dto.chart;
import com.chartmania.model.Chart; // adegua il package

import lombok.Data;

@Data
public class ChartsResponseDTO {
    private Long id;
    private String name;
    private String type;
    private String fileName;

    public ChartsResponseDTO(Long id, String name, String type, String fileName) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.fileName = fileName;
    }

    public static ChartsResponseDTO fromEntity(Chart chart) {
        return new ChartsResponseDTO(chart.getId(), chart.getName(),chart.getType(),chart.getFileName());
    }

    
}
