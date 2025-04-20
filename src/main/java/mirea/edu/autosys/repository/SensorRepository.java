package mirea.edu.autosys.repository;

    import java.util.Optional;

    import org.springframework.data.jpa.repository.JpaRepository;

    import mirea.edu.autosys.model.Sensor;

public interface SensorRepository extends JpaRepository<Sensor, Integer> {
    Optional<Sensor> findBySensorOpcuaEndpoint(String endpoint);
}
