package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "actuators")
@Getter
public class Actuator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer actuatorId;
    private String actuatorOpcuaEndpoint;
    private Integer actuatorStatus;

    @OneToMany(mappedBy = "actuator", cascade = CascadeType.ALL)
    private List<ActuatorParams> params = new ArrayList<>();

    public Actuator() {}
    public Actuator(String actuatorOpcuaEndpoint, Integer actuatorStatus) {
        this.actuatorOpcuaEndpoint = actuatorOpcuaEndpoint;
        this.actuatorStatus = actuatorStatus;
    }
}
