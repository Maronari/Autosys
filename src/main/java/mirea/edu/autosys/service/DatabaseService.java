package mirea.edu.autosys.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import mirea.edu.autosys.model.NodeTrend;
import mirea.edu.autosys.model.Sensor;
import mirea.edu.autosys.model.SensorParam;
import mirea.edu.autosys.repository.AutosysRepo;
import mirea.edu.autosys.repository.NodeTrendsRepository;
import mirea.edu.autosys.repository.SensorParamsRepository;
import mirea.edu.autosys.repository.SensorRepository;

@Service
public class DatabaseService {
    @Autowired
    private AutosysRepo repository;
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
    

    public NodeTrend saveSensorValue(String endpoint, Double value, LocalDateTime timestamp) {
        Sensor sensor = sensorRepository.findBySensorOpcuaEndpoint(endpoint)
            .orElseThrow(() -> new RuntimeException("Sensor not found for endpoint: " + endpoint));

        SensorParam param = sensorParamsRepository.findFirstBySensor(sensor)
            .orElseThrow(() -> new RuntimeException("No SensorParam found for sensor_id: " + sensor.getSensor_id()));

        NodeTrend trend = new NodeTrend(sensor.getSensor_id(), value, timestamp);
        return repository.save(trend);
    }
}
