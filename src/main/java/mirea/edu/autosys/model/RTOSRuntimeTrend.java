package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "RTOSRuntimeTrends")
public class RTOSRuntimeTrend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer RTOSId;

    @ManyToOne
    @JoinColumn(name = "sensor_id")
    private Sensor sensor;

    private Integer task_id;
    private Integer runtime_record_id;
    private Integer task_priority;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime taskLastUpdate;
    private Long task_run_time_ticks;

    public RTOSRuntimeTrend() {}

    public RTOSRuntimeTrend(Sensor sensor, Integer taskId, Integer runtimeRecordId, Integer taskPriority, LocalDateTime taskLastUpdate, Long taskRunTimeTicks) {
        this.sensor = sensor;
        this.task_id = taskId;
        this.runtime_record_id = runtimeRecordId;
        this.task_priority = taskPriority;
        this.taskLastUpdate = taskLastUpdate;
        this.task_run_time_ticks = taskRunTimeTicks;
    }

    // getters and setters
}
