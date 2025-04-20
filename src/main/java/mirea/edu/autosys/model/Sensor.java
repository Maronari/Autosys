package mirea.edu.autosys.model;


import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "\"Sensors\"")
@Getter
public class Sensor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer sensor_id;

    @Column(name = "sensor_opcua_endpoint")
    private String sensorOpcuaEndpoint;
    
    @Column(name = "sensor_status")
    private Integer sensorStatus;

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<SystemLog> logs = new ArrayList<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<SensorParam> params = new ArrayList<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<RTOSRuntimeTrend> NodeTrends = new ArrayList<>();

    @OneToMany(mappedBy = "sensor", cascade = CascadeType.ALL)
    private List<RTOSRuntimeTrend> rtosTrends = new ArrayList<>();

    public Sensor() {}

    public Sensor(String sensorOpcuaEndpoint, Integer sensorStatus) {
        this.sensorOpcuaEndpoint = sensorOpcuaEndpoint;
        this.sensorStatus = sensorStatus;
    }
}
