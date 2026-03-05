package com.chartmania.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.NoSuchFileException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.chartmania.dto.GenericResponseDTO;
import com.chartmania.dto.chart.CreateChartDatasetsRequest;
import com.chartmania.dto.chart.CreateChartPointsRequest;
import com.chartmania.dto.chart.CreateChartRequest;
import com.chartmania.dto.chart.GetChartRequest.ChartDTO;
import com.chartmania.infrastructure.LocalStorageManager;
import com.chartmania.model.Chart;
import com.chartmania.model.ChartData;
import com.chartmania.model.ChartDataSet;
import com.chartmania.model.User;
import com.chartmania.repository.ChartDataRepository;
import com.chartmania.repository.ChartDatasetRepository;
import com.chartmania.repository.ChartRepository;
import com.chartmania.repository.UserRepository;
import com.chartmania.util.imageUtil;

import jakarta.transaction.Transactional;

@Service
public class ChartService {
    @Autowired
    private ChartRepository chartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ChartDatasetRepository chartDatasetsRepository;

    @Autowired
    private ChartDataRepository chartDataRepository;

    @Autowired
    private LocalStorageManager localStorageManager;

    @Value("${chart.upload.directory}")
    private String chartImagesPath;

    public Page<Chart> getCharts(int page, int pageSize) {
        Pageable pageable = PageRequest.of(page, pageSize);
        return chartRepository.findAll(pageable);
    }

    @Transactional
    public GenericResponseDTO createChart(Long userId, CreateChartRequest data) {
        try {
            Optional<User> userOpt = userRepository.findById(userId);
            if (!userOpt.isPresent()) {
                return new GenericResponseDTO(true, "couldn't create chart");
            }

            String base64Image = data.getChartImage();
            String fileName = storeImageFile(base64Image);

            User user = userOpt.get();
            Chart chart = chartRepository.save(new Chart(user, data.getName(),
                    data.getType(), fileName));

            List<CreateChartDatasetsRequest> datasetsRequest = data.getDatasets();

            for (CreateChartDatasetsRequest dataSetRequest : datasetsRequest) {
                ChartDataSet chartDataSet = chartDatasetsRepository
                        .save(new ChartDataSet(dataSetRequest.getName(), chart));

                List<ChartData> points = dataSetRequest.getData().stream()
                        .map(pointReq -> new ChartData(pointReq.getLabel(), pointReq.getX(),
                                pointReq.getY(), pointReq.getColor(),
                                chartDataSet))
                        .toList();

                chartDataRepository.saveAll(points);
            }

            return new GenericResponseDTO(true, "Chart created");
        } catch (Exception e) {

            System.out.println(e);
            return new GenericResponseDTO(true, "couldn't create chart");
        }
    }

    @Transactional
    public GenericResponseDTO updateChart(Long userId, Long chartId, CreateChartRequest data) {
        try {
            Chart chart = chartRepository.findByIdAndUser_Id(chartId, userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

            // TODO: delete previous file
            String oldFileName = chart.getFileName();
            if (oldFileName != null) {
                deleteImageFile(oldFileName);
            }

            String base64Image = data.getChartImage();
            String fileName = storeImageFile(base64Image);
            chart.setName(data.getName());
            chart.setType(data.getType());
            chart.setFileName(fileName);

            List<CreateChartDatasetsRequest> datasetsRequest = data.getDatasets();

            // Get all dataset ids
            Set<Long> datasetsIds = datasetsRequest.stream()
                    .map(CreateChartDatasetsRequest::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());

            // Delete if these not longer exist
            List<Long> missingDatasetsIds = chartDatasetsRepository.findMissingIds(chartId, datasetsIds,
                    datasetsIds.isEmpty());
            Set<Long> missingDataSetsIdsUnique = new HashSet<>(missingDatasetsIds);

            chartDataRepository.deleteByChartDataSetIds(missingDataSetsIdsUnique);
            chartDatasetsRepository.deleteMissing(chartId, datasetsIds, datasetsIds.isEmpty());

            for (CreateChartDatasetsRequest dataSetRequest : datasetsRequest) {
                ChartDataSet dataset = syncDataSet(dataSetRequest, chart, chartId);

                List<CreateChartPointsRequest> points = dataSetRequest.getData();
                Set<Long> pointsIds = points.stream()
                        .map(CreateChartPointsRequest::getId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());

                chartDataRepository.deleteMissing(dataset.getId(), pointsIds, pointsIds.isEmpty());

                List<ChartData> existingPoints = chartDataRepository.findByChartDataSetId(dataset.getId());
                for (CreateChartPointsRequest pointReq : points) {
                    syncPoint(pointReq, dataset, existingPoints);
                }
            }

            return new GenericResponseDTO(true, "Chart updated");
        } catch (Exception e) {
            System.out.println(e);
            return new GenericResponseDTO(true, "couldn't update chart");
        }
    }

    public Resource getChartImage(Long userId, Long chartId) throws NoSuchFileException, MalformedURLException {
        Optional<Chart> opt = chartRepository.findByIdAndUser_Id(chartId, userId);
        if (opt.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

        Chart chart = opt.get();
        String fileName = chart.getFileName();
        Resource fileResource = localStorageManager.getFile(chartImagesPath, fileName);
        return fileResource;
    }

    @Transactional
    public GenericResponseDTO deleteChart(Long userId, Long chartId) {
        try {
            Optional<Chart> opt = chartRepository.findByIdAndUser_Id(chartId, userId);
            if (opt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            Chart chart = opt.get();
            chartRepository.deleteById(chart.getId());

            // chartRepository.deleteById(chartId);
            return new GenericResponseDTO(true, "Chart deleted");
        } catch (Exception e) {
            return new GenericResponseDTO<>(false, "couldn't delete chart");
        }
    }

    public GenericResponseDTO<ChartDTO> getChart(Long userId, Long chartId) throws ResponseStatusException {
        try {
            Optional<Chart> opt = chartRepository.findByIdAndUser_Id(chartId, userId);
            if (opt.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND);
            }

            Chart chart = opt.get();
            ChartDTO chartResponse = ChartDTO.fromEntity(chart);
            return new GenericResponseDTO<>(true, "", chartResponse);
        } catch (Exception e) {
            return new GenericResponseDTO<>(false, "");
        }
    }

    // Sync methods
    private ChartDataSet syncDataSet(CreateChartDatasetsRequest req, Chart chart, Long chartId) {
        if (req.getId() != null) {
            ChartDataSet dataset = chartDatasetsRepository.findByIdAndChartId(req.getId(), chartId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid Dataset id"));
            dataset.setName(req.getName());
            return dataset;
        }
        return chartDatasetsRepository.save(new ChartDataSet(req.getName(), chart));
    }

    private void syncPoint(CreateChartPointsRequest req, ChartDataSet dataset, List<ChartData> existingPoints) {
        if (req.getId() != null) {
            ChartData point = existingPoints.stream()
                    .filter(p -> p.getId().equals(req.getId()))
                    .findFirst()
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Not valid point id"));
            point.setLabel(req.getLabel());
            point.setX(req.getX());
            point.setY(req.getY());
            point.setColor(req.getColor());
        } else {
            chartDataRepository.save(new ChartData(req.getLabel(), req.getX(), req.getY(), req.getColor(), dataset));
        }
    }

    private String storeImageFile(String base64Image) throws IOException {
        byte[] imageByte = imageUtil.convertBase64ImageInBytes(base64Image);
        return this.localStorageManager.saveFile(imageByte, "jpg",
                chartImagesPath);
    }

    private void deleteImageFile(String fileName) throws IOException {
        this.localStorageManager.deleteFile(chartImagesPath + "/" + fileName);
    }

}
