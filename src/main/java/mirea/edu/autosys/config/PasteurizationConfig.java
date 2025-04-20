package mirea.edu.autosys.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import mirea.edu.autosys.service.PasterizationService;

@Configuration
public class PasteurizationConfig {
    public static final double PASTEURIZATION_TEMP = 75.0;
    public static final double HOLDING_TEMP_MIN = 70.0;
    public static final double FINAL_COOLING_TEMP_MAX = 6.0;
    public static final double FINAL_COOLING_TEMP_MIN = 4.0;
    public static final int HOLDING_TIME_SECONDS = 30;
    public static final double RECUPERATION_MIN_TEMP_DIFF = 10.0;

    // Параметры регулирования нагрева
    public static final double MAX_HEATING_WATER_FLOW = 100.0; // %
    public static final double MIN_HEATING_WATER_FLOW = 0.0; // %
    public static final double HEATING_FLOW_STEP_BIG = 20.0; // %
    public static final double HEATING_FLOW_STEP_SMALL = 10.0; // %

    // Параметры регулирования охлаждения
    public static final double MAX_COOLING_WATER_FLOW = 100.0; // %
    public static final double MIN_COOLING_WATER_FLOW = 0.0; // %
    public static final double COOLING_FLOW_STEP_BIG = 15.0; // %
    public static final double COOLING_FLOW_STEP_SMALL = 5.0; // %

    @Bean
    public PasterizationService pasterizationService() {
        return new PasterizationService();
    }
}
