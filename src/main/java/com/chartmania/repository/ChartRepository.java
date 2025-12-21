package com.chartmania.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.chartmania.model.Chart;


public interface ChartRepository extends JpaRepository<Chart,Long>{
   
}
