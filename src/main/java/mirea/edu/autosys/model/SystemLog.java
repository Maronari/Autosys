package mirea.edu.autosys.model;

import java.sql.Date;
import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "SystemLogs")
public class SystemLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logId;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "log_code")
    private LogCode logCode;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    private String message;

    public SystemLog() {}

    public SystemLog(Sensor sensor, LogCode logCode, LocalDateTime timestamp, String message) {
        this.sensor = sensor;
        this.logCode = logCode;
        this.timestamp = timestamp;
        this.message = message;
    }

    // getters and setters
}
