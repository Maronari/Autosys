package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "RTOSTask")
public class RTOSTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;
    private String taskName;

    @OneToMany(mappedBy = "taskId", cascade = CascadeType.ALL)
    private List<RTOSRuntimeTrends> rtosTrends = new ArrayList<>();

    public RTOSTask() {}
    public RTOSTask(String taskName) {
        this.taskName = taskName;
    }
}
