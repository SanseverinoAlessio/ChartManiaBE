package com.chartmania.dto.chart;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateChartPointsRequest {
    @Nullable
    private Long id;

    @NotNull
    private String label;

    private Double x;

    @NotNull
    private Double y;

    @NotNull
    private String color;
}
