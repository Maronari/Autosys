package mirea.edu.autosys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

import mirea.edu.autosys.model.NodeTrends;
import mirea.edu.autosys.service.DatabaseService;

@RestController
@RequestMapping("/api/pasteurization")
public class PasteurizationDataController {

    private final DatabaseService databaseService;

    public PasteurizationDataController(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    @GetMapping("/historical-data")
    public ResponseEntity<Map<String, Object>> getHistoricalData(
            @RequestParam(defaultValue = "24") int hours) {
        
        // Получаем данные из сервиса
        List<NodeTrends> temperatureData = databaseService.getTemperatureData(hours);
        List<NodeTrends> pressureData = databaseService.getPressureData(hours);
        List<NodeTrends> flowData = databaseService.getFlowData(hours);
        
        // Форматируем данные для фронтенда
        Map<String, Object> response = new HashMap<>();
        
        response.put("temperature", formatDataForCharts(temperatureData));
        response.put("pressure", formatDataForCharts(pressureData));
        response.put("flow", formatDataForCharts(flowData));
        
        // Добавляем текущие значения
        if (!temperatureData.isEmpty()) {
            response.put("currentTemperature", temperatureData.get(temperatureData.size() - 1).getNodeValue());
        }
        
        if (!pressureData.isEmpty()) {
            response.put("currentPressure", pressureData.get(pressureData.size() - 1).getNodeValue());
        }
        
        if (!flowData.isEmpty()) {
            response.put("currentFlow", flowData.get(flowData.size() - 1).getNodeValue());
            response.put("totalFlow", databaseService.getTotalFlow());
        }
        
        return ResponseEntity.ok(response);
    }
    
    private Map<String, Object> formatDataForCharts(List<NodeTrends> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        
        List<String> labels = new java.util.ArrayList<>();
        List<Double> values = new java.util.ArrayList<>();
        
        for (NodeTrends item : data) {
            labels.add(item.getTimestamp().format(formatter));
            values.add((Double) item.getNodeValue());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("labels", labels);
        result.put("values", values);
        
        return result;
    }
    
    @GetMapping("/system-status")
    public ResponseEntity<Map<String, Object>> getSystemStatus() {
        Map<String, Object> status = databaseService.getCurrentSystemStatus();
        return ResponseEntity.ok(status);
    }
}