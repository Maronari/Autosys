package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "actuator_params")
@Getter
public class ActuatorParams {
    @Id
    private String nodeId;

    @ManyToOne
    @JoinColumn(name = "actuatorId")
    private Actuator actuator;

    private String paramName;
    private Integer paramValue;
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;

    public ActuatorParams() {}
    public ActuatorParams(String nodeId, Actuator actuator, String paramName, Integer paramValue, LocalDateTime timestamp) {
        this.nodeId = nodeId;
        this.actuator = actuator;
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.timestamp = timestamp;
    }
}
