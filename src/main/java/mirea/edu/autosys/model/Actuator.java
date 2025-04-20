package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "Actuators")
public class Actuator {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer actuatorId;
    private String actuatorOpcuaEndpoint;
    private Integer actuatorStatus;

    @OneToMany(mappedBy = "actuator", cascade = CascadeType.ALL)
    private List<ActuatorParam> params = new ArrayList<>();

    public Actuator() {}
    public Actuator(String actuatorOpcuaEndpoint, Integer actuatorStatus) {
        this.actuatorOpcuaEndpoint = actuatorOpcuaEndpoint;
        this.actuatorStatus = actuatorStatus;
    }
}
