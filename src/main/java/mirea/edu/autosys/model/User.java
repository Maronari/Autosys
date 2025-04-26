package mirea.edu.autosys.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "User")
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private UserType type;

    private String name;
    private String login;
    private String password;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<UserLogs> logs = new ArrayList<>();

    public User() {}
    public User(UserType type, String name, String login, String password) {
        this.type = type;
        this.name = name;
        this.login = login;
        this.password = password;
    }
}
