package com.chartmania.repository;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.chartmania.model.Chart;


public interface ChartRepository extends JpaRepository<Chart,Long>, JpaSpecificationExecutor<Chart>{

    


}
