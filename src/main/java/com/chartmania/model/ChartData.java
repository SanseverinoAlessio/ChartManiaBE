package com.chartmania.model;

import java.time.Instant;

import org.hibernate.annotations.SoftDelete;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "chart_data")
@SoftDelete(columnName = "deleted")
@Getter
@NoArgsConstructor()
@Data
public class ChartData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "label", nullable = true, length = 200)
    private String label;

    @Setter
    @Column(name = "x_value", nullable = false)
    private Double x;

    @Setter
    @Column(name = "y_value", nullable = false)
    private Double y;

    @Setter
    @Column(name = "color", nullable = true, length = 32)
    private String color;

    @Setter(AccessLevel.PUBLIC)
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "chart_dataset_id", nullable = false, updatable = false)
    private ChartDataSet chartDataSet;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    public ChartData(String label, Double x, Double y, String color,ChartDataSet chartDataSet) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.color = color;
        this.chartDataSet = chartDataSet;
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
}
