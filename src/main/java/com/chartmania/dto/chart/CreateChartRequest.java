package com.chartmania.dto.chart;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateChartRequest {
    @NotBlank(message = "Insert a name")
    private String name;

    @NotBlank
    private String type;

    @NotBlank
    private String chartImage; //Base64 image

    private List<String> labels = new ArrayList<>();
    List<CreateChartDatasetsRequest> datasets = new ArrayList<>();

    private Instant createdAt;
    private Instant updatedAt;
}
