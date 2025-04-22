package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "\"OPCNodeGroup\"")
public class OPCNodeGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "group_id")
    private Integer groupId;

    @Column(name = "group_name")
    private String groupName;

    @OneToMany(mappedBy = "groupId", cascade = CascadeType.ALL)
    private List<SensorParams> params = new ArrayList<>();

    public OPCNodeGroup() {}

    public OPCNodeGroup(String groupName) {
        this.groupName = groupName;
    }

    // getters and setters
}
