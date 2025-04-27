package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "sensor_trends")
@AllArgsConstructor
@NoArgsConstructor
public class SensorTrends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trend_id")
    private Integer trendId;

    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id", nullable = false)
    private Sensor sensorId;

    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @Column(name = "node_value", nullable = false)
    private Double nodeValue;

    public SensorTrends(Sensor sensor, Double nodeValue, LocalDateTime timestamp) {
        this.sensorId = sensor;
        this.timestamp = timestamp;
        this.nodeValue = nodeValue;
    }
}
