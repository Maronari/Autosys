package mirea.edu.autosys.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "UserLogs")
public class UserLogs {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "action_id")
    private UserAction action;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime timestamp;
    private String description;

    public UserLogs() {}
    public UserLogs(User user, UserAction action, LocalDateTime timestamp, String description) {
        this.user = user;
        this.action = action;
        this.timestamp = timestamp;
        this.description = description;
    }
}
