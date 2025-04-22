package mirea.edu.autosys.repository;

import mirea.edu.autosys.model.NodeTrends;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AutosysRepo extends JpaRepository<NodeTrends, Long> {
}