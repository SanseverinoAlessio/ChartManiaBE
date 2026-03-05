package com.chartmania.repository;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.chartmania.model.Chart;


public interface ChartRepository extends JpaRepository<Chart,Long>, JpaSpecificationExecutor<Chart>{
    Optional<Chart> findByIdAndUser_Id(Long chartId,Long userId);

}
