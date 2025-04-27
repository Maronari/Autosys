package mirea.edu.autosys.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "sensor_params")
@Getter
public class SensorParams {

    @Id
    @Column(name = "param_id")
    private Integer paramId;

    @Column(name = "node_id")
    private String nodeId;

    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id")
    private Sensor sensorId;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private OPCNodeGroup groupId;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_min_value")
    private Integer paramMinValue;

    @Column(name = "param_max_value")
    private Integer paramMaxValue;

    public SensorParams() {}

    public SensorParams(Sensor sensor, String paramName, Integer paramMinValue, Integer paramMaxValue) {
        this.sensorId = sensor;
        this.paramName = paramName;
        this.paramMinValue = paramMinValue;
        this.paramMaxValue = paramMaxValue;
    }

    // getters and setters
}
