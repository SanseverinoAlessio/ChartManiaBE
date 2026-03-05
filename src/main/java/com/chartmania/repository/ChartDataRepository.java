package com.chartmania.repository;
import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chartmania.model.ChartData;

public interface ChartDataRepository extends JpaRepository<ChartData,Long>, JpaSpecificationExecutor<ChartData>{
    
    @Modifying
    @Query("""
                delete from ChartData d
                where d.chartDataSet.id in :ids
            """)
    int deleteByChartDataSetIds(@Param("ids") Set<Long> ids);

    int deleteByChartDataSetId(Long datasetId);

    @Modifying
    @Query("""
                delete from ChartData d
                where d.chartDataSet.id = :datasetId
                  and (:idsEmpty = true or d.id not in :ids)
            """)
    int deleteMissing(@Param("datasetId") Long datasetId,
            @Param("ids") Set<Long> ids,
            @Param("idsEmpty") boolean idsEmpty);

    List<ChartData> findByChartDataSetId(Long datasetId);
}
