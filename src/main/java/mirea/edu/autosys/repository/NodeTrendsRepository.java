package mirea.edu.autosys.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import mirea.edu.autosys.model.NodeTrend;

public interface NodeTrendsRepository extends JpaRepository<NodeTrend, Integer> {
}