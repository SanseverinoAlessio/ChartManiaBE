package com.chartmania.model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.SoftDelete;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chart_datasets")
@NoArgsConstructor
@SoftDelete(columnName = "deleted")
@Getter
public class ChartDataSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false, length = 200)
    private String name;

    @Setter(AccessLevel.PACKAGE)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "chart_id", nullable = false, updatable = false)
    private Chart chart;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "chartDataSet", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ChartData> points = new ArrayList<>();

    public ChartDataSet(String name, Chart chart) {
        this.name = name;
        this.chart = chart;
    }
    

    @PrePersist
    protected void onCreate() {
        Instant now = Instant.now();
        this.createdAt = now;
        this.updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }


    public void addPoint(ChartData chartData) {
        points.add(chartData);
        chartData.setChartDataSet(this);
    }

    public void removePoint(ChartData chartData) {
        points.remove(chartData);
        chartData.setChartDataSet(null);
    }


}
