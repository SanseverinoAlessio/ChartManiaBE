package com.chartmania.dto.chart.GetChartRequest;
import java.time.Instant;

import com.chartmania.model.ChartData;

public record ChartPointDTO(
        Long id,
        String label,
        Double x,
        Double y,
        String color,
        Instant createdAt,
        Instant updatedAt
) {
    public static ChartPointDTO fromEntity(ChartData point) {
        if (point == null) return null;

        return new ChartPointDTO(
                point.getId(),
                point.getLabel(),
                point.getX(),
                point.getY(),
                point.getColor(),
                point.getCreatedAt(),
                point.getUpdatedAt()
        );
    }
}

