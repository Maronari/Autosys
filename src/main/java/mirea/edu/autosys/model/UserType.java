package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "UserType")
public class UserType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String description;
    private Boolean privilege;

    @OneToMany(mappedBy = "type", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    public UserType() {}
    public UserType(String name, String description, Boolean privilege) {
        this.name = name;
        this.description = description;
        this.privilege = privilege;
    }
}
