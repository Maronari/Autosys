package mirea.edu.autosys.repository;

    import java.util.Optional;

    import org.springframework.data.jpa.repository.JpaRepository;

    import mirea.edu.autosys.model.Sensors;

public interface SensorRepository extends JpaRepository<Sensors, Integer> {
    Optional<Sensors> findBySensorOpcuaEndpoint(String endpoint);
}
