package mirea.edu.autosys.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.time.format.DateTimeFormatter;

import mirea.edu.autosys.config.OpcUaConfig;
import mirea.edu.autosys.model.SensorTrends;
import mirea.edu.autosys.service.DatabaseService;

@RestController
@RequestMapping("/api/pasteurization")
public class PasteurizationDataController {

    @Autowired
    private DatabaseService databaseService;

    @GetMapping("/historical-data/pasteurization")
    public ResponseEntity<Map<String, Object>> getHistoricalDataPasteurization(
            @RequestParam(defaultValue = "24") int hours) {

        // Получаем данные из сервиса
        List<SensorTrends> temperatureData = databaseService.getTemperatureData(hours);
        List<SensorTrends> pressureData = databaseService.getTemperatureData(hours);
        List<SensorTrends> flowData = databaseService.getTemperatureData(hours);

        Map<String, Object> pasteurizationTemperature = formatDataForCharts(
                temperatureData.stream()
                        .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                        .collect(Collectors.toList()));
        Map<String, Object> pasteurizationPresure = formatDataForCharts(
            pressureData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));
        Map<String, Object> pasteurizationFlow = formatDataForCharts(
            flowData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));

        // Форматируем данные для фронтенда
        Map<String, Object> pasteurizationData = new HashMap<>();
        pasteurizationData.put("temperature", pasteurizationTemperature);
        pasteurizationData.put("pressure", pasteurizationPresure);
        pasteurizationData.put("flow", pasteurizationFlow);

        Map<String, Object> response = new HashMap<>();
        response.put("pasteurization", pasteurizationData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/historical-data/heating")
    public ResponseEntity<Map<String, Object>> getHistoricalDataHeating(
            @RequestParam(defaultValue = "24") int hours) {
            
        // Получаем данные из сервиса
        List<SensorTrends> temperatureData = databaseService.getTemperatureData(hours);
        List<SensorTrends> pressureData = databaseService.getTemperatureData(hours);
        List<SensorTrends> flowData = databaseService.getTemperatureData(hours);
        
        Map<String, Object> heatingTemperature = formatDataForCharts(
            temperatureData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));
        Map<String, Object> heatingPresure = formatDataForCharts(
            pressureData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));
        Map<String, Object> heatingFlow = formatDataForCharts(
            flowData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));

        Map<String, Object> heatingData = new HashMap<>();
        heatingData.put("temperature", heatingTemperature);
        heatingData.put("pressure", heatingPresure);
        heatingData.put("flow", heatingFlow);

        Map<String, Object> response = new HashMap<>();
        response.put("heating", heatingData);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/historical-data/cooling")
    public ResponseEntity<Map<String, Object>> getHistoricalDataCooling(
            @RequestParam(defaultValue = "24") int hours) {
            
        // Получаем данные из сервиса
        List<SensorTrends> temperatureData = databaseService.getTemperatureData(hours);
        List<SensorTrends> pressureData = databaseService.getTemperatureData(hours);
        List<SensorTrends> flowData = databaseService.getTemperatureData(hours);
        
        Map<String, Object> coolingTemperature = formatDataForCharts(
            temperatureData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));
        Map<String, Object> coolingPresure = formatDataForCharts(
            pressureData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));
        Map<String, Object> coolingFlow = formatDataForCharts(
            flowData.stream()
                    .filter(data -> data.getSensorId().getSensorOpcuaEndpoint().equals(OpcUaConfig.endpointUrls[0]))
                    .collect(Collectors.toList()));

        Map<String, Object> heatingData = new HashMap<>();
        heatingData.put("temperature", coolingTemperature);
        heatingData.put("pressure", coolingPresure);
        heatingData.put("flow", coolingFlow);

        Map<String, Object> response = new HashMap<>();
        response.put("cooling", heatingData);

        return ResponseEntity.ok(response);
    }

    private Map<String, Object> formatDataForCharts(List<SensorTrends> data) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");

        List<String> labels = new java.util.ArrayList<>();
        List<Double> values = new java.util.ArrayList<>();

        for (SensorTrends item : data) {
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