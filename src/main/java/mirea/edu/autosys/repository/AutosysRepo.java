package mirea.edu.autosys.repository;

import mirea.edu.autosys.model.Temperature;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutosysRepo extends JpaRepository<Temperature, Long> {
}