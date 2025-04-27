package mirea.edu.autosys.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "report")
@Getter
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private Integer reportId;

    @OneToOne
    @JoinColumn(name = "alarm_id", referencedColumnName = "alarm_id", nullable = false)
    private Integer alarmId;
}
