package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "\"NodeTrends\"")
public class NodeTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trend_id")
    private Integer trend_id;

    @Column(name = "sensor_id", nullable = false)
    private Integer sensor;

    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @Column(name = "node_value", nullable = false)
    private Double node_value;

    public NodeTrend(Integer sensor, Double node_value, LocalDateTime timestamp) {
        this.sensor = sensor;
        this.timestamp = timestamp;
        this.node_value = node_value;
    }
}
