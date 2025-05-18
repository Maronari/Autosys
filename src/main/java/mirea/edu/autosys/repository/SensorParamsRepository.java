package mirea.edu.autosys.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import mirea.edu.autosys.model.Sensor;
import mirea.edu.autosys.model.SensorParams;

public interface SensorParamsRepository extends JpaRepository<SensorParams, String> {
    Optional<SensorParams> findFirstBySensorId(Sensor sensorId);

    @Query("SELECT sp.sensorId FROM SensorParams sp WHERE sp.paramName = :paramName")
    Optional<Sensor> findSensorByParamName(String paramName);
}
