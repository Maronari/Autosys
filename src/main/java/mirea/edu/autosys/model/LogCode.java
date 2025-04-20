package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "LogCode")
public class LogCode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer logCode;

    private String logDescription;

    @OneToMany(mappedBy = "logCode", cascade = CascadeType.ALL)
    private List<SystemLog> logs = new ArrayList<>();

    public LogCode() {}

    public LogCode(String logDescription) {
        this.logDescription = logDescription;
    }

    // getters and setters
}
