package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "user_role")
@Getter
public class UserRole {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer roleId;
    private String name;
    private String description;
    private Boolean privilege;

    @OneToMany(mappedBy = "roleId", cascade = CascadeType.ALL)
    private List<User> users = new ArrayList<>();

    public UserRole() {}
    public UserRole(String name, String description, Boolean privilege) {
        this.name = name;
        this.description = description;
        this.privilege = privilege;
    }
}
