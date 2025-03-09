package mirea.edu.autosys.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;

@Configuration
public class InfluxDBConfig {

    @Value("${spring.influx.url}")
    @Autowired
    private String url;

    @Value("${spring.influx.token}")
    @Autowired
    private String token;

    @Value("${spring.influx.org}")
    @Autowired
    private String org;

    @Value("${spring.influx.bucket}")
    @Autowired
    private String bucket;

    @Bean
    public InfluxDBClient influxDBClient() throws Exception {
        return InfluxDBClientFactory.create(
                url,
                token.toCharArray(),
                org,
                bucket);
    }
}