package com.chartmania.integration;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.chartmania.dto.chart.CreateChartDatasetsRequest;
import com.chartmania.dto.chart.CreateChartPointsRequest;
import com.chartmania.dto.chart.CreateChartRequest;
import com.chartmania.repository.ChartRepository;

import tools.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Sql(scripts = "/tests/chartcontrollerintegration/data.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/tests/chartcontrollerintegration/data-cleanup.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
public class ChartControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChartRepository chartRepository;

    private static final long USER_ID = 1;
    private static final long CHART_ID = 1;
    private static final String CHART_IMAGE = "data:image/jpeg;base64,AAAA";

    @Test
    public void testIfUserCanGetHisChart() throws Exception {
        mockMvc.perform(get("/api/personal-area/charts/" + CHART_ID)
                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)))
                .andExpect(jsonPath("$.data", notNullValue()));
    }

    @Test
    public void testIfUserCannotGetChartOfAnotherUser() throws Exception {
        mockMvc.perform(get("/api/personal-area/charts/" + 2)
                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.success", is(false)));
    }

    @Test
    public void testCreateChart() throws Exception {
        CreateChartPointsRequest point = new CreateChartPointsRequest();
        point.setLabel("Point 1");
        point.setX(10.0);
        point.setY(20.0);
        point.setColor("#ff0000");

        CreateChartDatasetsRequest dataset = new CreateChartDatasetsRequest();
        dataset.setName("Dataset 1");
        dataset.setData(List.of(point));

        CreateChartRequest request = new CreateChartRequest();
        request.setName("Integration Test Chart");
        request.setType("bar");
        request.setChartImage(CHART_IMAGE);
        request.setDatasets(List.of(dataset));

        mockMvc.perform(post("/api/personal-area/charts/create")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        boolean exists = chartRepository.findAll().stream()
                .anyMatch(c -> "Integration Test Chart".equals(c.getName()));
        assertTrue(exists);
    }

    @Test
    public void testUpdateChart() throws Exception {
        CreateChartPointsRequest point = new CreateChartPointsRequest();
        point.setLabel("Point 1");
        point.setX(10.0);
        point.setY(20.0);
        point.setColor("#ff0000");

        CreateChartDatasetsRequest dataset = new CreateChartDatasetsRequest();
        dataset.setId(1L);
        dataset.setName("Dataset 1 Updated");
        dataset.setData(List.of(point));

        CreateChartRequest request = new CreateChartRequest();
        request.setName("Updated");
        request.setType("line");
        request.setChartImage(CHART_IMAGE);
        request.setDatasets(List.of(dataset));

        mockMvc.perform(put("/api/personal-area/charts/" + CHART_ID)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        String updatedName = chartRepository.findByIdAndUser_Id(CHART_ID, USER_ID)
                .orElseThrow()
                .getName();
        assertEquals("Updated", updatedName);
    }

    @Test
    public void testDeleteChart() throws Exception {
        mockMvc.perform(delete("/api/personal-area/charts/" + CHART_ID)
                .with(jwt().jwt(jwt -> jwt.claim("userId", USER_ID))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success", is(true)));

        assertTrue(chartRepository.findByIdAndUser_Id(CHART_ID, USER_ID).isEmpty());
    }
}
