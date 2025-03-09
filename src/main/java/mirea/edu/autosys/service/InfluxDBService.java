package mirea.edu.autosys.service;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;
import org.springframework.stereotype.Service;
import java.time.Instant;

@Service
public class InfluxDBService {

    private final InfluxDBClient influxDBClient;

    public InfluxDBService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    public void writeTemperature(double temperature, String nodeId) {
        Point point = Point.measurement("temperature")
                .addTag("nodeId", nodeId)
                .addField("value", temperature)
                .time(Instant.now(), WritePrecision.MS);

        influxDBClient.getWriteApiBlocking().writePoint(point);
    }
}

