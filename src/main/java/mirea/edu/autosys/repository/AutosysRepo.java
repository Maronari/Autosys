package mirea.edu.autosys.repository;

import mirea.edu.autosys.model.NodeTrend;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutosysRepo extends JpaRepository<NodeTrend, Long> {
}