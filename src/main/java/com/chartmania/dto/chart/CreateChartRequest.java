package com.chartmania.dto.chart;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateChartRequest {
    @NotBlank(message = "Insert a name")
    private String name;

    @NotBlank
    private String type;

    @NotNull
    private String json;


    public CreateChartRequest() {}

    public CreateChartRequest(String name, String type, String json) {
        this.name = name;
        this.type = type;
        this.json = json;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
