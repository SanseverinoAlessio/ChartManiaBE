package com.chartmania.dto.chart.GetChartRequest;
import java.time.Instant;
import java.util.List;

import com.chartmania.model.ChartDataSet;

public record ChartDataSetDTO(
        Long id,
        String name,
        Instant createdAt,
        Instant updatedAt,
        List<ChartPointDTO> points) {

    public static ChartDataSetDTO fromEntity(ChartDataSet dataSet) {
        if (dataSet == null)
            return null;

        return new ChartDataSetDTO(
                dataSet.getId(),
                dataSet.getName(),
                dataSet.getCreatedAt(),
                dataSet.getUpdatedAt(),
                dataSet.getPoints() == null
                        ? List.of()
                        : dataSet.getPoints().stream().map(ChartPointDTO::fromEntity).toList());
    }
}
