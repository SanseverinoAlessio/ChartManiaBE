package com.chartmania.dto.chart;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateChartDatasetsRequest {
    @Nullable
    private Long id;

    @NotNull
    private String name;

    private List<CreateChartPointsRequest> data = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
