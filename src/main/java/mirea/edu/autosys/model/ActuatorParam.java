package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "ActuatorParams")
public class ActuatorParam {
    @Id
    private String nodeId;

    @ManyToOne
    @JoinColumn(name = "actuator_id")
    private Actuator actuator;

    private String paramName;
    private Integer paramValue;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    public ActuatorParam() {}
    public ActuatorParam(String nodeId, Actuator actuator, String paramName, Integer paramValue, LocalDateTime timestamp) {
        this.nodeId = nodeId;
        this.actuator = actuator;
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.timestamp = timestamp;
    }
}
