package mirea.edu.autosys.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "\"SensorParams\"")
@Getter
public class SensorParam {

    @Id
    @Column(name = "param_id")
    private Integer paramId;

    @Column(name = "node_id")
    private String nodeId;

    @ManyToOne
    @JoinColumn(name = "sensor_id", referencedColumnName = "sensor_id")
    private Sensor sensor;

    @ManyToOne
    @JoinColumn(name = "group_id")
    private OPCNodeGroup group;

    @Column(name = "param_name")
    private String paramName;

    @Column(name = "param_min_value")
    private Integer paramMinValue;

    @Column(name = "param_max_value")
    private Integer paramMaxValue;

    public SensorParam() {}

    public SensorParam(Sensor sensor, OPCNodeGroup group, String paramName, Integer paramMinValue, Integer paramMaxValue) {
        this.sensor = sensor;
        this.group = group;
        this.paramName = paramName;
        this.paramMinValue = paramMinValue;
        this.paramMaxValue = paramMaxValue;
    }

    // getters and setters
}
