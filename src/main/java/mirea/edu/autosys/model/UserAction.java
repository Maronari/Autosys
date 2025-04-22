package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "UserAction")
public class UserAction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;

    @OneToMany(mappedBy = "action", cascade = CascadeType.ALL)
    private List<UserLogs> logs = new ArrayList<>();

    public UserAction() {}
    public UserAction(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
