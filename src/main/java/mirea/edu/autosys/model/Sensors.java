package mirea.edu.autosys.model;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "\"Sensors\"")
@Getter
public class Sensors {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sensor_id")
    private Integer sensorId;

    @Column(name = "sensor_opcua_endpoint")
    private String sensorOpcuaEndpoint;
    
    @Column(name = "sensor_status")
    private Integer sensorStatus;

    @OneToMany(mappedBy = "sensorId", cascade = CascadeType.ALL)
    private List<SystemLogs> logs = new ArrayList<>();

    @OneToMany(mappedBy = "sensorId", cascade = CascadeType.ALL)
    private List<SensorParams> params = new ArrayList<>();

    @OneToMany(mappedBy = "sensorId", cascade = CascadeType.ALL)
    private List<RTOSRuntimeTrends> NodeTrends = new ArrayList<>();

    @OneToMany(mappedBy = "sensorId", cascade = CascadeType.ALL)
    private List<RTOSRuntimeTrends> rtosTrends = new ArrayList<>();

    public Sensors() {}

    public Sensors(String sensorOpcuaEndpoint, Integer sensorStatus) {
        this.sensorOpcuaEndpoint = sensorOpcuaEndpoint;
        this.sensorStatus = sensorStatus;
    }
}
