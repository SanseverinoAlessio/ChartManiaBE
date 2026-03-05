package com.chartmania.dto.chart.GetChartRequest;
import java.time.Instant;
import java.util.List;

import com.chartmania.model.Chart;

public record ChartDTO(
        Long id,
        String name,
        String type,
        String fileName,
        Instant createdAt,
        Instant updatedAt,
        List<ChartDataSetDTO> datasets
) {
    public static ChartDTO fromEntity(Chart chart) {
        if (chart == null) return null;

        return new ChartDTO(
                chart.getId(),
                chart.getName(),
                chart.getType(),
                chart.getFileName(),
                chart.getCreatedAt(),
                chart.getUpdatedAt(),
                chart.getDatasets() == null
                        ? List.of()
                        : chart.getDatasets().stream().map(ChartDataSetDTO::fromEntity).toList()
        );
    }
}