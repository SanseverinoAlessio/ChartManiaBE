package com.chartmania.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.chartmania.model.ChartDataSet;

public interface ChartDatasetRepository
        extends JpaRepository<ChartDataSet, Long>, JpaSpecificationExecutor<ChartDataSet> {
    void deleteByChartId(Long chartId);

    Optional<ChartDataSet> findByIdAndChartId(Long id, Long chartId);

    @Query("""
              select d.id
              from ChartDataSet d
              where d.chart.id = :chartId
                and (:idsEmpty = true or d.id not in :ids)
            """)
    List<Long> findMissingIds(@Param("chartId") Long chartId,
            @Param("ids") Set<Long> ids,
            @Param("idsEmpty") boolean idsEmpty);

    @Modifying
    @Query("""
                delete from ChartDataSet d
                where d.chart.id = :chartId
                  and (:idsEmpty = true or d.id not in :ids)
            """)
    int deleteMissing(@Param("chartId") Long chartId,
            @Param("ids") Set<Long> ids,
            @Param("idsEmpty") boolean idsEmpty);

            

}
