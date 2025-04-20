package mirea.edu.autosys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mirea.edu.autosys.model.Sensor;
import mirea.edu.autosys.model.SensorParam;

public interface SensorParamsRepository extends JpaRepository<SensorParam, String> {
    Optional<SensorParam> findFirstBySensor(Sensor sensor);
}
