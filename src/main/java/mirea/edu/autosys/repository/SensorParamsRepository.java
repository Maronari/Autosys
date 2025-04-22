package mirea.edu.autosys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import mirea.edu.autosys.model.Sensors;
import mirea.edu.autosys.model.SensorParams;

public interface SensorParamsRepository extends JpaRepository<SensorParams, String> {
    Optional<SensorParams> findFirstBySensorId(Sensors sensorId);
}
