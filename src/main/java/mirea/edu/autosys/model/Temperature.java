package mirea.edu.autosys.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "temperature")
public class Temperature {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "endpoint", nullable = false)
    private String endpoint_url;
    
    @Column(name = "nodeid", nullable = false)
    private  String node_id;

    @Column(name = "value", nullable = false)
    private  Double temperature_value;

    public Temperature(String endpoint, String nodeId, Double value) {
        this.endpoint_url = endpoint;
        this.node_id = nodeId;
        this.temperature_value = value;
    }
}
