package mirea.edu.autosys.model;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "SystemLogs")
public class SystemLogs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensors sensorId;

    @ManyToOne
    @JoinColumn(name = "logCode")
    private LogCode logCode;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    private String message;

    public SystemLogs() {}

    public SystemLogs(Sensors sensor, LogCode logCode, LocalDateTime timestamp, String message) {
        this.sensorId = sensor;
        this.logCode = logCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    // getters and setters
}
