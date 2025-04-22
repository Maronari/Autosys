package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "RTOSRuntimeTrends")
public class RTOSRuntimeTrends {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer RTOSId;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensors sensorId;

    private Integer taskId;
    private Integer runtimeRecordId;
    private Integer taskPriority;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime taskLastUpdate;
    private Long taskRunTimeTicks;

    public RTOSRuntimeTrends() {}

    public RTOSRuntimeTrends(Sensors sensor, Integer taskId, Integer runtimeRecordId, Integer taskPriority, LocalDateTime taskLastUpdate, Long taskRunTimeTicks) {
        this.sensorId = sensor;
        this.taskId = taskId;
        this.runtimeRecordId = runtimeRecordId;
        this.taskPriority = taskPriority;
        this.taskLastUpdate = taskLastUpdate;
        this.taskRunTimeTicks = taskRunTimeTicks;
    }

    // getters and setters
}
