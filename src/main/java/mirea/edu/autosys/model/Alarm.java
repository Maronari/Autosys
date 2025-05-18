package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "alarms")
@Getter
public class Alarm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alarm_id")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id", nullable = false)
    private Sensor sensorId;

    @OneToOne
    @JoinColumn(name = "actuator_id", referencedColumnName = "actuator_id", nullable = false)
    private Actuator ActuatorId;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false)
    private User userId;

    @Column(name = "timestamp", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    @Column(name = "description", nullable = false)
    private String description;
}
