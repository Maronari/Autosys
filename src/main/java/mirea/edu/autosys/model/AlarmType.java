package mirea.edu.autosys.model;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table(name = "alarm_type")
@Getter
public class AlarmType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "type_id")
    private Integer typeId;

    @Column(name = "type_description")
    private String typeDescription;
}
