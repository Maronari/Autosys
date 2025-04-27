package mirea.edu.autosys.service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mirea.edu.autosys.model.SensorTrends;
import mirea.edu.autosys.model.Sensor;
import mirea.edu.autosys.model.SensorParams;
import mirea.edu.autosys.repository.NodeTrendsRepository;
import mirea.edu.autosys.repository.SensorParamsRepository;
import mirea.edu.autosys.repository.SensorRepository;

@Service
public class DatabaseService {

    private final SensorRepository sensorRepository;
    private final SensorParamsRepository sensorParamsRepository;
    private final NodeTrendsRepository nodeTrendsRepository;

    @Autowired
    public DatabaseService(SensorRepository sensorRepository,
                        SensorParamsRepository sensorParamsRepository,
                        NodeTrendsRepository nodeTrendsRepository) {
        this.sensorRepository = sensorRepository;
        this.sensorParamsRepository = sensorParamsRepository;
        this.nodeTrendsRepository = nodeTrendsRepository;
    }
    

    public SensorTrends saveSensorValue(String endpoint, Double value, LocalDateTime timestamp) {
        Sensor sensor = sensorRepository.findBySensorOpcuaEndpoint(endpoint)
            .orElseThrow(() -> new RuntimeException("Sensor not found for endpoint: " + endpoint));

        SensorParams param = sensorParamsRepository.findFirstBySensorId(sensor)
            .orElseThrow(() -> new RuntimeException("No SensorParam found for sensor_id: " + sensor.getSensorId()));

        SensorTrends trend = new SensorTrends(sensor, value, timestamp);
        return nodeTrendsRepository.save(trend);
    }


    /**
     * Получить данные температуры за указанное количество часов
     */
    public List<SensorTrends> getTemperatureData(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return nodeTrendsRepository.findBySensorTypeAndTimestampAfter("Temp", startTime);
    }
    
    /**
     * Получить данные давления за указанное количество часов
     */
    public List<SensorTrends> getPressureData(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return nodeTrendsRepository.findBySensorTypeAndTimestampAfter("Pressure", startTime);
    }
    
    /**
     * Получить данные расхода за указанное количество часов
     */
    public List<SensorTrends> getFlowData(int hours) {
        LocalDateTime startTime = LocalDateTime.now().minusHours(hours);
        return nodeTrendsRepository.findBySensorTypeAndTimestampAfter("Flow", startTime);
    }
    
    /**
     * Получить общий расход жидкости
     */
    public Double getTotalFlow() {
        return nodeTrendsRepository.sumValuesBySensorType("total_flow");
    }
    
    /**
     * Получить текущий статус системы
     */
    public Map<String, Object> getCurrentSystemStatus() {
        Map<String, Object> status = new HashMap<>();
        
        // Получаем текущие значения из БД
        SensorTrends latestTemperature = nodeTrendsRepository.findLatestBySensorType("Temp").get(0);
        // NodeTrends latestPressure = nodeTrendsRepository.findLatestBySensorType("pressure", PageRequest.of(0, 1)).get(0);
        // NodeTrends latestFlow = nodeTrendsRepository.findLatestBySensorType("flow", PageRequest.of(0, 1)).get(0);
        
        // Получаем статус системы из БД или используем значение по умолчанию
        String systemStatus = "online"; // Здесь можно получить из БД или другого источника
        int processStage = 3; // Здесь можно получить из БД или другого источника
        
        // Заполняем объект статуса
        status.put("systemStatus", systemStatus);
        status.put("processStage", processStage);
        
        // Добавляем текущие значения телеметрии
        if (latestTemperature != null) {
            status.put("targetTemperature", 75.0); // Целевая температура (можно получить из БД)
        }
        
        // if (latestFlow != null) {
        //     status.put("targetFlow", 120.0); // Целевой расход (можно получить из БД)
        // }
        
        // Добавляем время процесса
        status.put("stageTime", 1800); // Время текущего этапа в секундах (можно получить из БД)
        status.put("totalTime", 7200); // Общее время процесса в секундах (можно получить из БД)
        
        return status;
    }
}
