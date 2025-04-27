package mirea.edu.autosys.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import mirea.edu.autosys.model.SensorTrends;

public interface NodeTrendsRepository extends JpaRepository<SensorTrends, Integer> {
    /**
     * Получить данные определенного типа за указанный период времени
     */
    @Query("""
        SELECT nt FROM SensorTrends nt
        JOIN nt.sensorId s
        JOIN s.params sp
        JOIN sp.groupId og
        WHERE og.groupName = :groupName
            AND nt.timestamp >= :startTime
        ORDER BY nt.timestamp ASC
    """)
    List<SensorTrends> findBySensorTypeAndTimestampAfter(
            @Param("groupName") String groupName, 
            @Param("startTime") LocalDateTime startTime);
    
    /**
     * Получить последнее значение для указанного типа сенсора
     */
    @Query("""
        SELECT nt FROM SensorTrends nt
        JOIN nt.sensorId s
        JOIN s.params sp
        JOIN sp.groupId og
        WHERE og.groupName = :groupName
        ORDER BY nt.timestamp DESC
    """)
    List<SensorTrends> findLatestBySensorType(@Param("groupName") String groupName);
    
    /**
     * Получить сумму всех значений для указанного типа сенсора
     */
    @Query("""
        SELECT SUM(nt.nodeValue) FROM SensorTrends nt
        JOIN nt.sensorId s
        JOIN s.params sp
        JOIN sp.groupId og
        WHERE og.groupName = :groupName
    """)
    Double sumValuesBySensorType(@Param("groupName") String groupName);
}