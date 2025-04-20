package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "RTOSTask")
public class RTOSTask {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer task_id;
    private String task_name;

    @OneToMany(mappedBy = "task_id", cascade = CascadeType.ALL)
    private List<RTOSRuntimeTrend> rtosTrends = new ArrayList<>();

    public RTOSTask() {}
    public RTOSTask(String taskName) {
        this.task_name = taskName;
    }
}
